package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.SqueezerRecipe;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.EnergyRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.api.GroovyLog;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class Squeezer extends VirtualizedRegistry<SqueezerRecipe> {

    public Squeezer() {
        super();
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> SqueezerRecipe.recipeList.removeIf(r -> r == recipe));
        SqueezerRecipe.recipeList.addAll(restoreFromBackup());
    }

    public void add(SqueezerRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            SqueezerRecipe.recipeList.add(recipe);
        }
    }

    public SqueezerRecipe add(FluidStack fluidOutput, @Nonnull ItemStack itemOutput, IIngredient input, int energy) {
        SqueezerRecipe recipe = new SqueezerRecipe(fluidOutput, itemOutput, ImmersiveEngineering.toIngredientStack(input), energy);
        addScripted(recipe);
        return recipe;
    }

    public boolean remove(SqueezerRecipe recipe) {
        if (SqueezerRecipe.recipeList.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public void removeByOutput(FluidStack fluidOutput) {
        if (IngredientHelper.isEmpty(fluidOutput)) {
            GroovyLog.msg("Error removing Immersive Engineering Squeezer recipe")
                    .add("fluid output must not be empty")
                    .error()
                    .post();
            return;
        }
        if (!SqueezerRecipe.recipeList.removeIf(recipe -> {
            if (fluidOutput.isFluidEqual(recipe.fluidOutput)) {
                addBackup(recipe);
                return true;
            }
            return false;
        })) {
            GroovyLog.msg("Error removing Immersive Engineering Squeezer recipe")
                    .add("no recipes found for %s", fluidOutput)
                    .error()
                    .post();
        }
    }

    public void removeByOutput(FluidStack fluidOutput, ItemStack itemOutput) {
        if (GroovyLog.msg("Error removing Immersive Engineering Squeezer recipe")
                .add(IngredientHelper.isEmpty(fluidOutput), () -> "fluid output must not be empty")
                .add(IngredientHelper.isEmpty(itemOutput), () -> "item input must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        if (!SqueezerRecipe.recipeList.removeIf(recipe -> {
            if (fluidOutput.isFluidEqual(recipe.fluidOutput) && recipe.input.matches(itemOutput)) {
                addBackup(recipe);
                return true;
            }
            return false;
        })) {
            GroovyLog.msg("Error removing Immersive Engineering Squeezer recipe")
                    .add("no recipes found for %s and %s", fluidOutput, itemOutput)
                    .error()
                    .post();
        }
    }

    public void removeByInput(ItemStack input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Immersive Engineering Squeezer recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return;
        }
        SqueezerRecipe recipe = SqueezerRecipe.findRecipe(input);
        if (recipe == null || !remove(recipe)) {
            GroovyLog.msg("Error removing Immersive Engineering Squeezer recipe")
                    .add("no recipes found for %s", input)
                    .error()
                    .post();
        }
    }

    public SimpleObjectStream<SqueezerRecipe> streamRecipes() {
        return new SimpleObjectStream<>(SqueezerRecipe.recipeList).setRemover(this::remove);
    }

    public void removeAll() {
        SqueezerRecipe.recipeList.forEach(this::addBackup);
        SqueezerRecipe.recipeList.clear();
    }

    private static class RecipeBuilder extends EnergyRecipeBuilder<SqueezerRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Refinery recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 1);
            validateFluids(msg, 0, 0, 0, 1);
        }

        @Override
        public @Nullable SqueezerRecipe register() {
            return ModSupport.IMMERSIVE_ENGINEERING.get().squeezer.add(fluidOutput.getOrEmpty(0), output.getOrEmpty(0), input.get(0), energy);
        }
    }
}
