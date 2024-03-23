package com.cleanroommc.groovyscript.compat.mods.rustic;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import rustic.common.crafting.ICondenserRecipe;
import rustic.common.crafting.Recipes;
import rustic.common.items.ModItems;
import rustic.common.util.ElixirUtils;

import java.util.Collection;

@RegistryDescription
public class Alchemy extends VirtualizedRegistry<ICondenserRecipe> {

    public Alchemy() {
        super(Alias.generateOfClass(Alchemy.class).andGenerate("Condenser"));
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:stone'), item('minecraft:gold_ingot')).output(item('minecraft:clay') * 4).time(20)"),
            @Example(value = ".input(item('minecraft:stone'), item('minecraft:gold_ingot'), item('minecraft:diamond')).bottle(item('minecraft:torch')).advanced().effect(new PotionEffect(potion('minecraft:night_vision'), 3600, 1))", imports = "net.minecraft.potion.PotionEffect"),
            @Example(".input(item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone')).modifier(item('minecraft:clay')).fluidInput(fluid('lava') * 500).advanced().output(item('minecraft:diamond'))"),
            @Example(".input(item('minecraft:cobblestone'), item('minecraft:cobblestone')).fluidInput(fluid('lava') * 25).bottle(item('minecraft:bucket')).output(item('minecraft:lava_bucket'))")
    })
    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        Recipes.condenserRecipes.removeAll(removeScripted());
        Recipes.condenserRecipes.addAll(restoreFromBackup());
    }

    public void add(ICondenserRecipe recipe) {
        Recipes.condenserRecipes.add(recipe);
        addScripted(recipe);
    }

    public boolean remove(ICondenserRecipe recipe) {
        addBackup(recipe);
        return Recipes.condenserRecipes.remove(recipe);
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('rustic:elixir').withNbt(['ElixirEffects': [['Effect': 'minecraft:night_vision', 'Duration': 3600, 'Amplifier': 0]]])"))
    public boolean removeByOutput(IIngredient output) {
        return Recipes.condenserRecipes.removeIf(entry -> {
            if (output.test(entry.getResult())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('minecraft:sugar')"))
    public boolean removeByInput(IIngredient input) {
        return Recipes.condenserRecipes.removeIf(entry -> {
            if (entry.getInputs().stream().flatMap(Collection::stream).anyMatch(input)) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        Recipes.condenserRecipes.forEach(this::addBackup);
        Recipes.condenserRecipes.clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<ICondenserRecipe> streamRecipes() {
        return new SimpleObjectStream<>(Recipes.condenserRecipes).setRemover(this::remove);
    }

    @Property(property = "input", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "2 or 3", type = Comp.Type.LTE)})
    @Property(property = "fluidInput", defaultValue = "fluid('water') * 125", valid = {@Comp(value = "0", type = Comp.Type.GTE), @Comp(value = "1", type = Comp.Type.LTE)})
    @Property(property = "output", valid = {@Comp(value = "0", type = Comp.Type.GTE), @Comp(value = "1", type = Comp.Type.LTE)})
    public static class RecipeBuilder extends AbstractRecipeBuilder<ICondenserRecipe> {

        @Property(defaultValue = "IIngredient.EMPTY")
        private IIngredient modifier = IIngredient.EMPTY;
        @Property(defaultValue = "IIngredient.EMPTY")
        private IIngredient bottle = IIngredient.EMPTY;
        @Property(defaultValue = "400")
        private int time = 400;
        @Property
        private boolean advanced;

        @RecipeBuilderMethodDescription
        public RecipeBuilder effect(PotionEffect effect) {
            var output = new ItemStack(ModItems.ELIXIR);
            ElixirUtils.addEffect(effect, output);
            this.output.add(output);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder modifier(IIngredient modifier) {
            this.modifier = modifier;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder bottle(IIngredient bottle) {
            this.bottle = bottle;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder advanced() {
            this.advanced = !advanced;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder advanced(boolean advanced) {
            this.advanced = advanced;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Rustic Alchemy recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, advanced ? 3 : 2, 1, 1);
            msg.add(msg.hasSubMessages(), "advanced requires a maximum of 3 inputs, while basic requires a maximum of 2 inputs");
            validateFluids(msg, 0, 1, 0, 0);
            msg.add(!modifier.isEmpty() && !advanced, "modifier can only be defined if advanced is true");
            msg.add(time <= 0, "time must be an integer greater than 0, yet it was {}", time);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable ICondenserRecipe register() {
            if (!validate()) return null;

            ICondenserRecipe recipe = new CondenserRecipe(output.get(0), input, modifier, bottle,
                                                          fluidInput.isEmpty() ? new FluidStack(FluidRegistry.WATER, 125) : fluidInput.getOrEmpty(0),
                                                          time, advanced);

            ModSupport.RUSTIC.get().alchemy.add(recipe);
            return recipe;
        }
    }

}
