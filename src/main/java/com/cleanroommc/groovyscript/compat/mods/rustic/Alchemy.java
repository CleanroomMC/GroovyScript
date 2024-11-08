package com.cleanroommc.groovyscript.compat.mods.rustic;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
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
public class Alchemy extends StandardListRegistry<ICondenserRecipe> {

    public Alchemy() {
        super(Alias.generateOfClass(Alchemy.class).andGenerate("Condenser"));
    }

    @Override
    public Collection<ICondenserRecipe> getRecipes() {
        return Recipes.condenserRecipes;
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


    @MethodDescription(example = @Example("item('rustic:elixir').withNbt(['ElixirEffects': [['Effect': 'minecraft:night_vision', 'Duration': 3600, 'Amplifier': 0]]])"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(entry -> {
            if (output.test(entry.getResult())) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:sugar')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(entry -> {
            if (entry.getInputs().stream().flatMap(Collection::stream).anyMatch(input)) {
                addBackup(entry);
                return true;
            }
            return false;
        });
    }

    @Property(property = "input", comp = @Comp(gte = 1, unique = "groovyscript.wiki.rustic.alchemy.input.required"))
    @Property(property = "fluidInput", defaultValue = "fluid('water') * 125", comp = @Comp(gte = 0, lte = 1))
    @Property(property = "output", comp = @Comp(gte = 0, lte = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<ICondenserRecipe> {

        @Property(defaultValue = "IIngredient.EMPTY")
        private IIngredient modifier = IIngredient.EMPTY;
        @Property(defaultValue = "IIngredient.EMPTY")
        private IIngredient bottle = IIngredient.EMPTY;
        @Property(defaultValue = "400")
        private int time = 400;
        @Property
        private boolean advanced;

        @RecipeBuilderMethodDescription(field = "output")
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
        protected int getMaxItemInput() {
            return 1;
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

            ICondenserRecipe recipe = new CondenserRecipe(
                    output.get(0),
                    input,
                    modifier,
                    bottle,
                    fluidInput.isEmpty() ? new FluidStack(FluidRegistry.WATER, 125) : fluidInput.getOrEmpty(0),
                    time,
                    advanced);

            ModSupport.RUSTIC.get().alchemy.add(recipe);
            return recipe;
        }
    }
}
