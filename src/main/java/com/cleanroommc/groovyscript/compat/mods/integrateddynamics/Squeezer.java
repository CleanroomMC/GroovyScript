package com.cleanroommc.groovyscript.compat.mods.integrateddynamics;

import com.cleanroommc.groovyscript.api.GroovyLog;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Squeezer extends VirtualizedRegistry<IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DummyPropertiesComponent>> {

    public Squeezer() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder().basic();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(org.cyclops.integrateddynamics.block.BlockSqueezer.getInstance().getRecipeRegistry().allRecipes()::remove);
        restoreFromBackup().forEach(org.cyclops.integrateddynamics.block.BlockSqueezer.getInstance().getRecipeRegistry().allRecipes()::add);
    }

    public void add(IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DummyPropertiesComponent> recipe) {
        this.add(recipe, true);
    }

    public void add(IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DummyPropertiesComponent> recipe, boolean add) {
        if (recipe == null) return;
        addScripted(recipe);
        if (add) org.cyclops.integrateddynamics.block.BlockSqueezer.getInstance().getRecipeRegistry().allRecipes().add(recipe);
    }

    public boolean remove(IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DummyPropertiesComponent> recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        org.cyclops.integrateddynamics.block.BlockSqueezer.getInstance().getRecipeRegistry().allRecipes().remove(recipe);
        return true;
    }

    public boolean removeByInput(ItemStack input) {
        return org.cyclops.integrateddynamics.block.BlockSqueezer.getInstance().getRecipeRegistry().allRecipes().removeIf(r -> {
            if (r.getInput().getIngredient().test(input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    public void removeAll() {
        org.cyclops.integrateddynamics.block.BlockSqueezer.getInstance().getRecipeRegistry().allRecipes().forEach(this::addBackup);
        org.cyclops.integrateddynamics.block.BlockSqueezer.getInstance().getRecipeRegistry().allRecipes().clear();
    }

    public SimpleObjectStream<IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DummyPropertiesComponent>> streamRecipes() {
        return new SimpleObjectStream<>(org.cyclops.integrateddynamics.block.BlockSqueezer.getInstance().getRecipeRegistry().allRecipes())
                .setRemover(this::remove);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DummyPropertiesComponent>> {

        private final List<IngredientRecipeComponent> output = new ArrayList<>();
        private int duration = 10;
        private boolean basic;
        private boolean mechanical;

        public RecipeBuilder basic(boolean is) {
            this.basic = is;
            return this;
        }

        public RecipeBuilder basic() {
            this.basic = !basic;
            return this;
        }

        public RecipeBuilder mechanical(boolean is) {
            this.mechanical = is;
            return this;
        }

        public RecipeBuilder mechanical() {
            this.mechanical = !mechanical;
            return this;
        }

        public RecipeBuilder output(ItemStack output, float chance) {
            IngredientRecipeComponent target = new IngredientRecipeComponent(output);
            target.setChance(chance);
            this.output.add(target);
            return this;
        }

        public RecipeBuilder output(ItemStack output) {
            this.output.add(new IngredientRecipeComponent(output));
            return this;
        }

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
        }

        @Override
        public @Nullable IRecipe<IngredientRecipeComponent, IngredientsAndFluidStackRecipeComponent, DummyPropertiesComponent> register() {
            if (!validate()) return null;

            if (basic) {
                ModSupport.INTEGRATED_DYNAMICS.get().squeezer.add(
                        org.cyclops.integrateddynamics.block.BlockSqueezer.getInstance().getRecipeRegistry().registerRecipe(
                                new IngredientRecipeComponent(input.get(0).toMcIngredient()),
                                new IngredientsAndFluidStackRecipeComponent(output, fluidOutput.getOrEmpty(0)),
                                new DummyPropertiesComponent()
                        ), false);
            }
            if (mechanical) {
                ModSupport.INTEGRATED_DYNAMICS.get().mechanicalSqueezer.add(
                        org.cyclops.integrateddynamics.block.BlockMechanicalSqueezer.getInstance().getRecipeRegistry().registerRecipe(
                                new IngredientRecipeComponent(input.get(0).toMcIngredient()),
                                new IngredientsAndFluidStackRecipeComponent(output, fluidOutput.getOrEmpty(0)),
                                new DurationRecipeProperties(duration)
                        ), false);
            }
            return null;
        }
    }
}
