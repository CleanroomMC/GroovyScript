package com.cleanroommc.groovyscript.compat.mods.integrateddynamics;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.cyclops.cyclopscore.recipe.custom.api.IRecipe;
import org.cyclops.cyclopscore.recipe.custom.component.DurationRecipeProperties;
import org.cyclops.cyclopscore.recipe.custom.component.IngredientAndFluidStackRecipeComponent;
import org.cyclops.integrateddynamics.Configs;
import org.cyclops.integrateddynamics.block.BlockDryingBasin;
import org.cyclops.integrateddynamics.block.BlockDryingBasinConfig;
import org.cyclops.integrateddynamics.block.BlockMechanicalDryingBasin;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class DryingBasin extends VirtualizedRegistry<IRecipe<IngredientAndFluidStackRecipeComponent, IngredientAndFluidStackRecipeComponent, DurationRecipeProperties>> {

    @Override
    public boolean isEnabled() {
        return Configs.isEnabled(BlockDryingBasinConfig.class);
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay')).fluidInput(fluid('water') * 500).fluidOutput(fluid('lava') * 2000).mechanical().duration(5)"),
            @Example(".output(item('minecraft:clay')).fluidInput(fluid('water') * 2000)")
    }, requirement = @Property(property = "basic", defaultValue = "true"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder().basic();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(BlockDryingBasin.getInstance().getRecipeRegistry().allRecipes()::remove);
        restoreFromBackup().forEach(BlockDryingBasin.getInstance().getRecipeRegistry().allRecipes()::add);
    }

    public void add(IRecipe<IngredientAndFluidStackRecipeComponent, IngredientAndFluidStackRecipeComponent, DurationRecipeProperties> recipe) {
        this.add(recipe, true);
    }

    public void add(IRecipe<IngredientAndFluidStackRecipeComponent, IngredientAndFluidStackRecipeComponent, DurationRecipeProperties> recipe, boolean add) {
        if (recipe == null) return;
        addScripted(recipe);
        if (add) BlockDryingBasin.getInstance().getRecipeRegistry().allRecipes().add(recipe);
    }

    public boolean remove(IRecipe<IngredientAndFluidStackRecipeComponent, IngredientAndFluidStackRecipeComponent, DurationRecipeProperties> recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        BlockDryingBasin.getInstance().getRecipeRegistry().allRecipes().remove(recipe);
        return true;
    }

    @MethodDescription
    public boolean removeByInput(ItemStack input) {
        return BlockDryingBasin.getInstance().getRecipeRegistry().allRecipes().removeIf(r -> {
            if (r.getInput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription
    public boolean removeByOutput(ItemStack input) {
        return BlockDryingBasin.getInstance().getRecipeRegistry().allRecipes().removeIf(r -> {
            if (r.getOutput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        BlockDryingBasin.getInstance().getRecipeRegistry().allRecipes().forEach(this::addBackup);
        BlockDryingBasin.getInstance().getRecipeRegistry().allRecipes().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<IRecipe<IngredientAndFluidStackRecipeComponent, IngredientAndFluidStackRecipeComponent, DurationRecipeProperties>> streamRecipes() {
        return new SimpleObjectStream<>(BlockDryingBasin.getInstance().getRecipeRegistry().allRecipes())
                .setRemover(this::remove);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<IRecipe<IngredientAndFluidStackRecipeComponent, IngredientAndFluidStackRecipeComponent, DurationRecipeProperties>> {

        @Property("groovyscript.wiki.integrateddynamics.drying_basin.basic.value")
        private boolean basic;
        @Property("groovyscript.wiki.integrateddynamics.drying_basin.mechanical.value")
        private boolean mechanical;
        @Property(value = "groovyscript.wiki.integrateddynamics.drying_basin.duration.value", defaultValue = "10")
        private int duration = 10;

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
        public RecipeBuilder duration(int duration) {
            this.duration = duration;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Integrated Dynamics Drying Basin Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 1, 0, 1);
            validateFluids(msg, 0, 1, 0, 1);
            msg.add(input.isEmpty() && fluidInput.isEmpty(), "either input or fluidInput must have an entry, yet both were empty");
            msg.add(output.isEmpty() && fluidOutput.isEmpty(), "either output or fluidOutput must have an entry, yet both were empty");
            msg.add(duration < 0, "duration must be a non negative integer, yet it was {}", duration);
            msg.add(!basic && !mechanical, "either basic or mechanical must be true");
            msg.add(basic && !ModSupport.INTEGRATED_DYNAMICS.get().dryingBasin.isEnabled(), "basic is enabled, yet the Drying Basin is disabled via config");
            msg.add(mechanical && !ModSupport.INTEGRATED_DYNAMICS.get().mechanicalDryingBasin.isEnabled(), "mechanic is enabled, yet the Mechanical Drying Basin is disabled via config");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable IRecipe<IngredientAndFluidStackRecipeComponent, IngredientAndFluidStackRecipeComponent, DurationRecipeProperties> register() {
            if (!validate()) return null;

            ItemStack itemInput = input.isEmpty() ? ItemStack.EMPTY : IngredientHelper.toItemStack(input.get(0));

            if (basic) {
                ModSupport.INTEGRATED_DYNAMICS.get().dryingBasin.add(
                        BlockDryingBasin.getInstance().getRecipeRegistry().registerRecipe(
                                new IngredientAndFluidStackRecipeComponent(itemInput, true, fluidInput.getOrEmpty(0)),
                                new IngredientAndFluidStackRecipeComponent(output.get(0), fluidOutput.getOrEmpty(0)),
                                new DurationRecipeProperties(duration)
                        ), false);
            }
            if (mechanical) {
                ModSupport.INTEGRATED_DYNAMICS.get().mechanicalDryingBasin.add(
                        BlockMechanicalDryingBasin.getInstance().getRecipeRegistry().registerRecipe(
                                new IngredientAndFluidStackRecipeComponent(itemInput, true, fluidInput.getOrEmpty(0)),
                                new IngredientAndFluidStackRecipeComponent(output.getOrEmpty(0), fluidOutput.getOrEmpty(0)),
                                new DurationRecipeProperties(duration)
                        ), false);
            }
            return null;
        }
    }
}
