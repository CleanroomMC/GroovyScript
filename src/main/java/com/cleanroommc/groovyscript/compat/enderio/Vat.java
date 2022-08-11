package com.cleanroommc.groovyscript.compat.enderio;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.enderio.recipe.RecipeInput;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.helper.IngredientList;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
import com.cleanroommc.groovyscript.mixin.enderio.VatRecipeManagerAccessor;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import com.enderio.core.common.util.NNList;
import crazypants.enderio.base.recipe.*;
import crazypants.enderio.base.recipe.vat.VatRecipeManager;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Vat {

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void remove(FluidStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.LOG.error("Error removing EnderIO Vat recipe for empty output!");
            return;
        }
        int oldSize = VatRecipeManager.getInstance().getRecipes().size();
        VatRecipeManager.getInstance().getRecipes().removeIf(recipe -> {
            FluidStack recipeOutput = recipe.getOutputs()[0].getFluidOutput();
            return output.isFluidEqual(recipeOutput);
        });
        if (oldSize == VatRecipeManager.getInstance().getRecipes().size()) {
            GroovyLog.LOG.error("Could not find EnderIO Vat recipes with fluid output {}", output.getFluid().getName());
        }
    }

    @GroovyBlacklist
    public void onReload() {
        VatRecipeManagerAccessor accessor = (VatRecipeManagerAccessor) VatRecipeManager.getInstance();
        ReloadableRegistryManager.unmarkScriptRecipes(Vat.class).forEach(accessor.getRecipes()::remove);
        ReloadableRegistryManager.recoverRecipes(Vat.class).stream().map(Recipe.class::cast).forEach(accessor.getRecipes()::add);
    }

    public static class RecipeBuilder implements IRecipeBuilder<Recipe> {

        private FluidStack output;
        private FluidStack input;
        private float baseMultiplier = 1;
        private IngredientList<IIngredient> itemInputs1 = new IngredientList<>();
        private IngredientList<IIngredient> itemInputs2 = new IngredientList<>();
        private FloatList multipliers1 = new FloatArrayList();
        private FloatList multipliers2 = new FloatArrayList();
        private int energy = 0;
        protected RecipeLevel level = RecipeLevel.IGNORE;

        public RecipeBuilder input(FluidStack input) {
            this.input = input;
            return this;
        }

        public RecipeBuilder output(FluidStack output) {
            this.output = output;
            return this;
        }

        public RecipeBuilder baseMultiplier(float baseMultiplier) {
            this.baseMultiplier = baseMultiplier;
            return this;
        }

        public RecipeBuilder itemInputLeft(IIngredient ingredient, float multiplier) {
            itemInputs1.add(ingredient);
            multipliers1.add(multiplier);
            return this;
        }

        public RecipeBuilder itemInputRight(IIngredient ingredient, float multiplier) {
            itemInputs2.add(ingredient);
            multipliers2.add(multiplier);
            return this;
        }

        public RecipeBuilder tierNormal() {
            this.level = RecipeLevel.NORMAL;
            return this;
        }

        public RecipeBuilder tierEnhanced() {
            this.level = RecipeLevel.ADVANCED;
            return this;
        }

        public RecipeBuilder tierAny() {
            this.level = RecipeLevel.IGNORE;
            return this;
        }

        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public boolean validate() {
            GroovyLog.Msg msg = new GroovyLog.Msg("Error adding EnderIO Vat recipe").error();
            msg.add(IngredientHelper.isEmpty(input), () -> "fluid input must not be empty");
            msg.add(IngredientHelper.isEmpty(output), () -> "fluid output must not be empty");

            if (energy <= 0) energy = 5000;
            if (baseMultiplier <= 0) baseMultiplier = 1;

            if (msg.hasSubMessages()) {
                GroovyLog.LOG.log(msg);
                return false;
            }
            return true;
        }

        @Override
        public @Nullable Recipe register() {
            if (!validate()) return null;
            List<IRecipeInput> inputs = new ArrayList<>();
            for (int i = 0; i < itemInputs1.size(); i++) {
                IIngredient ingredient = itemInputs1.get(i);
                if (!IngredientHelper.isEmpty(ingredient)) {
                    inputs.add(new VatRecipeInput(ingredient, 0, multipliers1.get(i)));
                }
            }
            for (int i = 0; i < itemInputs2.size(); i++) {
                IIngredient ingredient = itemInputs2.get(i);
                if (!IngredientHelper.isEmpty(ingredient)) {
                    inputs.add(new VatRecipeInput(ingredient, 1, multipliers2.get(i)));
                }
            }
            inputs.add(new crazypants.enderio.base.recipe.RecipeInput(input, baseMultiplier));

            Recipe recipe = new Recipe(new RecipeOutput(output), energy, RecipeBonusType.NONE, level, inputs.toArray(new IRecipeInput[0]));
            VatRecipeManager.getInstance().addRecipe(recipe);
            NNList<Recipe> recipes = ((VatRecipeManagerAccessor) VatRecipeManager.getInstance()).getRecipes();
            ReloadableRegistryManager.markScriptRecipe(Vat.class, recipes.get(recipes.size() - 1)); // VatRecipeManager wraps the Recipe
            return recipe;
        }
    }

    public static class VatRecipeInput extends RecipeInput {

        private final float multiplier;

        public VatRecipeInput(IIngredient ing, int slot, float multiplier) {
            super(ing, slot);
            this.multiplier = multiplier;
        }

        @Override
        public float getMulitplier() {
            return multiplier;
        }
    }
}
