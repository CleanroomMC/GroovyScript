package com.cleanroommc.groovyscript.compat.mods.integrateddynamics;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.cyclops.cyclopscore.recipe.custom.api.IRecipe;
import org.cyclops.cyclopscore.recipe.custom.component.DummyPropertiesComponent;
import org.cyclops.cyclopscore.recipe.custom.component.DurationRecipeProperties;
import org.cyclops.cyclopscore.recipe.custom.component.IngredientRecipeComponent;
import org.cyclops.cyclopscore.recipe.custom.component.IngredientsAndFluidStackRecipeComponent;
import org.cyclops.integrateddynamics.Configs;
import org.cyclops.integrateddynamics.block.BlockMechanicalSqueezer;
import org.cyclops.integrateddynamics.block.BlockSqueezer;
import org.cyclops.integrateddynamics.block.BlockSqueezerConfig;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@RegistryDescription
public class Squeezer extends VirtualizedRegistry<IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DummyPropertiesComponent>> {

    @Override
    public boolean isEnabled() {
        return Configs.isEnabled(BlockSqueezerConfig.class);
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:clay_ball'), 1F).output(item('minecraft:clay_ball') * 2, 0.7F).output(item('minecraft:clay_ball') * 10, 0.2F).fluidOutput(fluid('lava') * 2000).mechanical().duration(5)"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay'), 0.5F)"),
            @Example(".input(item('minecraft:diamond')).fluidOutput(fluid('lava') * 10)")
    }, requirement = @Property(property = "basic", defaultValue = "true"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder().basic();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(BlockSqueezer.getInstance().getRecipeRegistry().allRecipes()::remove);
        restoreFromBackup().forEach(BlockSqueezer.getInstance().getRecipeRegistry().allRecipes()::add);
    }

    public void add(IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DummyPropertiesComponent> recipe) {
        this.add(recipe, true);
    }

    public void add(IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DummyPropertiesComponent> recipe, boolean add) {
        if (recipe == null) return;
        addScripted(recipe);
        if (add) BlockSqueezer.getInstance().getRecipeRegistry().allRecipes().add(recipe);
    }

    public boolean remove(IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DummyPropertiesComponent> recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        BlockSqueezer.getInstance().getRecipeRegistry().allRecipes().remove(recipe);
        return true;
    }

    @MethodDescription
    public boolean removeByInput(ItemStack input) {
        return BlockSqueezer.getInstance().getRecipeRegistry().allRecipes().removeIf(r -> {
            if (r.getInput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        BlockSqueezer.getInstance().getRecipeRegistry().allRecipes().forEach(this::addBackup);
        BlockSqueezer.getInstance().getRecipeRegistry().allRecipes().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DummyPropertiesComponent>> streamRecipes() {
        return new SimpleObjectStream<>(BlockSqueezer.getInstance().getRecipeRegistry().allRecipes())
                .setRemover(this::remove);
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "fluidOutput", valid = {@Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "1")})
    public static class RecipeBuilder extends AbstractRecipeBuilder<IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DummyPropertiesComponent>> {

        @Property(value = "groovyscript.wiki.integrateddynamics.squeezer.output.value", ignoresInheritedMethods = true, valid = {
                @Comp(type = Comp.Type.GTE, value = "0"), @Comp(type = Comp.Type.LTE, value = "3")})
        private final List<IngredientRecipeComponent> output = new ArrayList<>();
        @Property(value = "groovyscript.wiki.integrateddynamics.squeezer.duration.value", defaultValue = "10")
        private int duration = 10;
        @Property("groovyscript.wiki.integrateddynamics.squeezer.basic.value")
        private boolean basic;
        @Property("groovyscript.wiki.integrateddynamics.squeezer.mechanical.value")
        private boolean mechanical;

        @RecipeBuilderMethodDescription
        public RecipeBuilder basic(boolean is) {
            this.basic = is;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder basic() {
            this.basic = !basic;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder mechanical(boolean is) {
            this.mechanical = is;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder mechanical() {
            this.mechanical = !mechanical;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder output(ItemStack output, float chance) {
            IngredientRecipeComponent target = new IngredientRecipeComponent(output);
            target.setChance(chance);
            this.output.add(target);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder output(ItemStack output) {
            this.output.add(new IngredientRecipeComponent(output));
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder duration(int duration) {
            this.duration = duration;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Integrated Dynamics Squeezer Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 3);
            validateFluids(msg, 0, 0, 0, 1);
            msg.add(output.size() > 3, "output can have a maximum of 3 entries, yet had {} entries", output.size());
            msg.add(mechanical && duration < 0, "duration must be a non negative integer if mechanical is true, yet it was {}", duration);
            msg.add(output.isEmpty() && fluidOutput.isEmpty(), "either output or fluidOutput must have an entry, yet both were empty");
            msg.add(!basic && !mechanical, "either basic or mechanical must be true");
            msg.add(basic && !ModSupport.INTEGRATED_DYNAMICS.get().squeezer.isEnabled(), "basic is enabled, yet the Squeezer is disabled via config");
            msg.add(mechanical && !ModSupport.INTEGRATED_DYNAMICS.get().mechanicalSqueezer.isEnabled(), "mechanic is enabled, yet the Mechanical Squeezer is disabled via config");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DummyPropertiesComponent> register() {
            if (!validate()) return null;

            if (basic) {
                ModSupport.INTEGRATED_DYNAMICS.get().squeezer.add(
                        BlockSqueezer.getInstance().getRecipeRegistry().registerRecipe(
                                new IngredientRecipeComponent(input.get(0).toMcIngredient()),
                                new IngredientsAndFluidStackRecipeComponent(output, fluidOutput.getOrEmpty(0)),
                                new DummyPropertiesComponent()
                        ), false);
            }
            if (mechanical) {
                ModSupport.INTEGRATED_DYNAMICS.get().mechanicalSqueezer.add(
                        BlockMechanicalSqueezer.getInstance().getRecipeRegistry().registerRecipe(
                                new IngredientRecipeComponent(input.get(0).toMcIngredient()),
                                new IngredientsAndFluidStackRecipeComponent(output, fluidOutput.getOrEmpty(0)),
                                new DurationRecipeProperties(duration)
                        ), false);
            }
            return null;
        }
    }
}
