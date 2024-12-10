package com.cleanroommc.groovyscript.compat.mods.forestry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.core.mixin.forestry.FermenterRecipeManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import forestry.api.recipes.IFermenterRecipe;
import forestry.factory.recipes.FermenterRecipe;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class Fermenter extends ForestryRegistry<IFermenterRecipe> {

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        if (!isEnabled()) return;
        removeScripted().forEach(FermenterRecipeManagerAccessor.getRecipes()::remove);
        restoreFromBackup().forEach(FermenterRecipeManagerAccessor.getRecipes()::add);
    }

    public IFermenterRecipe add(FluidStack output, FluidStack input, IIngredient catalyst, int value, float modifier) {
        IFermenterRecipe recipe;
        if (catalyst instanceof OreDictIngredient oreDictIngredient)
            recipe = new FermenterRecipe(oreDictIngredient.getOreDict(), value, modifier, input.getFluid(), output);
        else recipe = new FermenterRecipe(catalyst.getMatchingStacks()[0], value, modifier, input.getFluid(), output);
        add(recipe);
        return recipe;
    }

    public void add(IFermenterRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        FermenterRecipeManagerAccessor.getRecipes().add(recipe);
    }

    public boolean remove(IFermenterRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        return FermenterRecipeManagerAccessor.getRecipes().remove(recipe);
    }

    public boolean removeByInput(FluidStack input) {
        if (FermenterRecipeManagerAccessor.getRecipes().removeIf(recipe -> {
            boolean found = recipe.getFluidResource().isFluidEqual(input);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Fermenter recipe")
                .add("could not find recipe with input {}", input)
                .error()
                .post();
        return false;
    }

    public boolean removeByCatalyst(IIngredient input) {
        if (FermenterRecipeManagerAccessor.getRecipes().removeIf(recipe -> {
            boolean found = input instanceof OreDictIngredient oreDictIngredient
                    ? recipe.getResourceOreName().equals(oreDictIngredient.getOreDict())
                    : recipe.getResource().isItemEqual(input.getMatchingStacks()[0]);
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Fermenter recipe")
                .add("could not find recipe with catalyst {}", input)
                .error()
                .post();
        return false;
    }

    public boolean removeByOutput(FluidStack output) {
        if (FermenterRecipeManagerAccessor.getRecipes().removeIf(recipe -> {
            boolean found = output.getFluid().getUnlocalizedName().equals(recipe.getOutput().getUnlocalizedName());
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Fermenter recipe")
                .add("could not find recipe with output {}", output)
                .error()
                .post();
        return false;
    }

    public void removeAll() {
        FermenterRecipeManagerAccessor.getRecipes().forEach(this::addBackup);
        FermenterRecipeManagerAccessor.getRecipes().clear();
    }

    public SimpleObjectStream<IFermenterRecipe> streamRecipes() {
        return new SimpleObjectStream<>(FermenterRecipeManagerAccessor.getRecipes()).setRemover(this::remove);
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<IFermenterRecipe> {

        protected int value = 100;
        protected float modifier = 1.0F;

        public RecipeBuilder value(int value) {
            this.value = Math.max(value, 1);
            return this;
        }

        public RecipeBuilder modifier(float modifier) {
            this.modifier = Math.max(modifier, 0.01F);
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Forestry Fermenter recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg, 1, 1, 1, 1);
        }

        @Override
        public @Nullable IFermenterRecipe register() {
            if (!validate()) return null;
            IFermenterRecipe recipe;
            IIngredient catalyst = input.get(0);
            if (catalyst instanceof OreDictIngredient oreDictIngredient)
                recipe = new FermenterRecipe(oreDictIngredient.getOreDict(), value, modifier, fluidInput.get(0).getFluid(), fluidOutput.get(0));
            else recipe = new FermenterRecipe(catalyst.getMatchingStacks()[0], value, modifier, fluidInput.get(0).getFluid(), fluidOutput.get(0));
            add(recipe);
            return recipe;
        }
    }
}
