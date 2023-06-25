package com.cleanroommc.groovyscript.compat.mods.forestry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.core.mixin.forestry.StillRecipeManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import forestry.api.recipes.IStillRecipe;
import forestry.factory.recipes.StillRecipe;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class Still extends ForestryRegistry<IStillRecipe> {

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        if (!isEnabled()) return;
        removeScripted().forEach(StillRecipeManagerAccessor.getRecipes()::remove);
        restoreFromBackup().forEach(StillRecipeManagerAccessor.getRecipes()::add);
    }

    public IStillRecipe add(FluidStack output, int time, FluidStack input) {
        IStillRecipe recipe = new StillRecipe(time, input, output);
        add(recipe);
        return recipe;
    }

    public void add(IStillRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        StillRecipeManagerAccessor.getRecipes().add(recipe);
    }

    public boolean remove(IStillRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        return StillRecipeManagerAccessor.getRecipes().remove(recipe);
    }

    public boolean removeByInput(FluidStack input) {
        if (StillRecipeManagerAccessor.getRecipes().removeIf(recipe -> {
            boolean found = recipe.getInput().isFluidEqual(input);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Still recipe")
                .add("could not find recipe with input {}", input)
                .error()
                .post();
        return false;
    }

    public boolean removeByOutput(FluidStack output) {
        if (StillRecipeManagerAccessor.getRecipes().removeIf(recipe -> {
            boolean found = recipe.getOutput().isFluidEqual(output);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Still recipe")
                .add("could not find recipe with output {}", output)
                .error()
                .post();
        return false;
    }

    public void removeAll() {
        StillRecipeManagerAccessor.getRecipes().forEach(this::addBackup);
        StillRecipeManagerAccessor.getRecipes().clear();
    }

    public SimpleObjectStream<IStillRecipe> streamRecipes() {
        return new SimpleObjectStream<>(StillRecipeManagerAccessor.getRecipes()).setRemover(this::remove);
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<IStillRecipe> {

        protected int time = 100;

        public RecipeBuilder time(int time) {
            this.time = Math.max(time, 1);
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Forestry Still recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 0, 0);
            validateFluids(msg, 1, 1, 1, 1);
        }

        @Override
        public @Nullable IStillRecipe register() {
            if (!validate()) return null;
            IStillRecipe recipe = new StillRecipe(time, fluidInput.get(0), fluidOutput.get(0));
            add(recipe);
            return recipe;
        }
    }
}
