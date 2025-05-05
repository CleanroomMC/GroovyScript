package com.cleanroommc.groovyscript.compat.mods.integrateddynamics;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.cyclops.cyclopscore.recipe.custom.api.IRecipe;
import org.cyclops.cyclopscore.recipe.custom.component.DurationRecipeProperties;
import org.cyclops.cyclopscore.recipe.custom.component.IngredientAndFluidStackRecipeComponent;
import org.cyclops.integrateddynamics.Configs;
import org.cyclops.integrateddynamics.block.BlockDryingBasin;
import org.cyclops.integrateddynamics.block.BlockDryingBasinConfig;
import org.cyclops.integrateddynamics.block.BlockMechanicalDryingBasin;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class DryingBasin extends StandardListRegistry<IRecipe<IngredientAndFluidStackRecipeComponent, IngredientAndFluidStackRecipeComponent, DurationRecipeProperties>> {

    @Override
    public boolean isEnabled() {
        return Configs.isEnabled(BlockDryingBasinConfig.class);
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay')).fluidInput(fluid('water') * 500).fluidOutput(fluid('lava') * 2000).mechanical().duration(5)"),
            @Example(".output(item('minecraft:clay')).fluidInput(fluid('water') * 2000)")
    }, override = @RecipeBuilderOverride(requirement = @Property(property = "basic", defaultValue = "true")))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder().basic();
    }

    @Override
    public Collection<IRecipe<IngredientAndFluidStackRecipeComponent, IngredientAndFluidStackRecipeComponent, DurationRecipeProperties>> getRecipes() {
        return BlockDryingBasin.getInstance().getRecipeRegistry().allRecipes();
    }

    @MethodDescription
    public boolean removeByInput(ItemStack input) {
        return getRecipes().removeIf(r -> {
            if (r.getInput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription
    public boolean removeByOutput(ItemStack input) {
        return getRecipes().removeIf(r -> {
            if (r.getOutput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
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
        protected int getMaxItemInput() {
            // More than 1 item cannot be placed (normal), ignores input stack size (mechanical)
            return 1;
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
                ModSupport.INTEGRATED_DYNAMICS.get().dryingBasin.addScripted(
                        BlockDryingBasin.getInstance()
                                .getRecipeRegistry()
                                .registerRecipe(
                                        new IngredientAndFluidStackRecipeComponent(itemInput, true, fluidInput.getOrEmpty(0)),
                                        new IngredientAndFluidStackRecipeComponent(output.get(0), fluidOutput.getOrEmpty(0)),
                                        new DurationRecipeProperties(duration)
                                ));
            }
            if (mechanical) {
                ModSupport.INTEGRATED_DYNAMICS.get().mechanicalDryingBasin.addScripted(
                        BlockMechanicalDryingBasin.getInstance()
                                .getRecipeRegistry()
                                .registerRecipe(
                                        new IngredientAndFluidStackRecipeComponent(itemInput, true, fluidInput.getOrEmpty(0)),
                                        new IngredientAndFluidStackRecipeComponent(output.getOrEmpty(0), fluidOutput.getOrEmpty(0)),
                                        new DurationRecipeProperties(duration)
                                ));
            }
            return null;
        }
    }
}
