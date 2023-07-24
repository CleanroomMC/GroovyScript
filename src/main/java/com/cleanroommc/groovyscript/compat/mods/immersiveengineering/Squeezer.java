package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.SqueezerRecipe;
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
import java.util.List;
import java.util.stream.Collectors;

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
        add(recipe);
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
                    .add("no recipes found for {}", fluidOutput)
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
        List<SqueezerRecipe> recipes = SqueezerRecipe.recipeList.stream().filter(r -> fluidOutput.isFluidEqual(r.fluidOutput) && r.itemOutput.isItemEqual(itemOutput)).collect(Collectors.toList());
        for (SqueezerRecipe recipe : recipes) {
            remove(recipe);
        }
        if (recipes.isEmpty()) {
            GroovyLog.msg("Error removing Immersive Engineering Squeezer recipe")
                    .add("no recipes found for {} and {}", fluidOutput, itemOutput)
                    .error()
                    .post();
        }
    }

    public void removeByOutput(ItemStack itemOutput) {
        if (GroovyLog.msg("Error removing Immersive Engineering Squeezer recipe")
                .add(IngredientHelper.isEmpty(itemOutput), () -> "item input must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        // "Condition 'r.itemOutput != null' is always 'true'" is a lie. It can be null, and if it is it *will* throw an NPE if we don't check against it.
        @SuppressWarnings("ConstantValue")
        List<SqueezerRecipe> recipes = SqueezerRecipe.recipeList.stream().filter(r -> r != null && r.itemOutput != null && r.itemOutput.isItemEqual(itemOutput)).collect(Collectors.toList());
        for (SqueezerRecipe recipe : recipes) {
            remove(recipe);
        }
        if (recipes.isEmpty()) {
            GroovyLog.msg("Error removing Immersive Engineering Squeezer recipe")
                    .add("no recipes found for {}", itemOutput)
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
                    .add("no recipes found for {}", input)
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

    private static class RecipeBuilder extends AbstractRecipeBuilder<SqueezerRecipe> {

        private int energy;

        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Refinery recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 1);
            validateFluids(msg, 0, 0, 0, 1);
            msg.add(fluidOutput.size() == 0 && output.size() == 0, "Either a fluid output or an item output must be defined");
        }

        @Override
        public @Nullable SqueezerRecipe register() {
            if (!validate()) return null;
            SqueezerRecipe recipe = new SqueezerRecipe(fluidOutput.getOrEmpty(0), output.getOrEmpty(0), ImmersiveEngineering.toIngredientStack(input.get(0)), energy);
            ModSupport.IMMERSIVE_ENGINEERING.get().squeezer.add(recipe);
            return recipe;
        }
    }
}
