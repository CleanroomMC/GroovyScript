package com.cleanroommc.groovyscript.compat.mods.enderio;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.enderio.recipe.RecipeInput;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientList;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.enderio.core.common.util.NNList;
import crazypants.enderio.base.recipe.*;
import crazypants.enderio.base.recipe.vat.VatRecipe;
import crazypants.enderio.base.recipe.vat.VatRecipeManager;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Vat extends VirtualizedRegistry<VatRecipe> {

    public Vat() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void add(Recipe recipe) {
        VatRecipeManager instance = VatRecipeManager.getInstance();
        instance.addRecipe(recipe);
        addScripted((VatRecipe) instance.getRecipes().get(instance.getRecipes().size() - 1));
    }

    public boolean remove(VatRecipe recipe) {
        if (recipe == null) return false;
        VatRecipeManager.getInstance().getRecipes().remove(recipe);
        addBackup(recipe);
        return true;
    }

    public void remove(FluidStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.get().error("Error removing EnderIO Vat recipe for empty output!");
            return;
        }
        int oldSize = VatRecipeManager.getInstance().getRecipes().size();
        VatRecipeManager.getInstance().getRecipes().removeIf(recipe -> {
            FluidStack recipeOutput = recipe.getOutputs()[0].getFluidOutput();
            if (output.isFluidEqual(recipeOutput)) addBackup((VatRecipe) recipe);
            return output.isFluidEqual(recipeOutput);
        });
        if (oldSize == VatRecipeManager.getInstance().getRecipes().size()) {
            GroovyLog.get().error("Could not find EnderIO Vat recipes with fluid output {}", output.getFluid().getName());
        }
    }

    @GroovyBlacklist
    public void onReload() {
        NNList<IRecipe> recipes = VatRecipeManager.getInstance().getRecipes();
        removeScripted().forEach(recipes::remove);
        recipes.addAll(restoreFromBackup());
    }

    public SimpleObjectStream<VatRecipe> streamRecipes() {
        return new SimpleObjectStream<>(VatRecipeManager.getInstance().getRecipes().stream().map(r -> (VatRecipe) r).collect(Collectors.toList()))
                .setRemover(this::remove);
    }

    public void removeAll() {
        VatRecipeManager.getInstance().getRecipes().forEach(r -> addBackup((VatRecipe) r));
        VatRecipeManager.getInstance().getRecipes().clear();
    }

    public static class RecipeBuilder implements IRecipeBuilder<Recipe> {

        private FluidStack output;
        private FluidStack input;
        private float baseMultiplier = 1;
        private final IngredientList<IIngredient> itemInputs1 = new IngredientList<>();
        private final IngredientList<IIngredient> itemInputs2 = new IngredientList<>();
        private final FloatList multipliers1 = new FloatArrayList();
        private final FloatList multipliers2 = new FloatArrayList();
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
            GroovyLog.Msg msg = GroovyLog.msg("Error adding EnderIO Vat recipe").error();
            msg.add(IngredientHelper.isEmpty(input), () -> "fluid input must not be empty");
            msg.add(IngredientHelper.isEmpty(output), () -> "fluid output must not be empty");

            if (energy <= 0) energy = 5000;
            if (baseMultiplier <= 0) baseMultiplier = 1;

            return !msg.postIfNotEmpty();
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
            ModSupport.ENDER_IO.get().vat.add(recipe);
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
