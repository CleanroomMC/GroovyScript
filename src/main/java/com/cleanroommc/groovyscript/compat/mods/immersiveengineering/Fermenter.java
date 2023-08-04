package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.FermenterRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class Fermenter extends VirtualizedRegistry<FermenterRecipe> {

    public Fermenter() {
        super();
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> FermenterRecipe.recipeList.removeIf(r -> r == recipe));
        FermenterRecipe.recipeList.addAll(restoreFromBackup());
    }

    public void add(FermenterRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            FermenterRecipe.recipeList.add(recipe);
        }
    }

    public FermenterRecipe add(FluidStack fluidOutput, @Nonnull ItemStack itemOutput, IIngredient input, int energy) {
        FermenterRecipe recipe = new FermenterRecipe(fluidOutput.copy(), itemOutput.copy(), ImmersiveEngineering.toIngredientStack(input), energy);
        add(recipe);
        return recipe;
    }

    public boolean remove(FermenterRecipe recipe) {
        if (FermenterRecipe.recipeList.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public void removeByOutput(FluidStack fluidOutput) {
        if (IngredientHelper.isEmpty(fluidOutput)) {
            GroovyLog.msg("Error removing Immersive Engineering Fermenter recipe")
                    .add("fluid output must not be empty")
                    .error()
                    .post();
        }
        if (!FermenterRecipe.recipeList.removeIf(recipe -> {
            if (recipe.fluidOutput.isFluidEqual(fluidOutput)) {
                addBackup(recipe);
                return true;
            }
            return false;
        })) {
            GroovyLog.msg("Error removing Immersive Engineering Fermenter recipe")
                    .add("no recipes found for {}", fluidOutput)
                    .error()
                    .post();
        }
    }

    public void removeByInput(ItemStack input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Immersive Engineering Fermenter recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
        }
        FermenterRecipe recipe = FermenterRecipe.findRecipe(input);
        if (recipe != null) {
            FermenterRecipe.recipeList.remove(recipe);
            addBackup(recipe);
        } else {
            GroovyLog.msg("Error removing Immersive Engineering Fermenter recipe")
                    .add("no recipes found for {}", input)
                    .error()
                    .post();
        }
    }

    public SimpleObjectStream<FermenterRecipe> streamRecipes() {
        return new SimpleObjectStream<>(FermenterRecipe.recipeList).setRemover(this::remove);
    }

    public void removeAll() {
        FermenterRecipe.recipeList.forEach(this::addBackup);
        FermenterRecipe.recipeList.clear();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<FermenterRecipe> {

        private int energy;

        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Fermenter recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 1);
            validateFluids(msg, 0, 0, 1, 1);
        }

        @Override
        public @Nullable FermenterRecipe register() {
            if (!validate()) return null;
            FermenterRecipe recipe = new FermenterRecipe(fluidOutput.get(0), output.getOrEmpty(0), input.get(0), energy);
            ModSupport.IMMERSIVE_ENGINEERING.get().fermenter.add(recipe);
            return recipe;
        }
    }
}
