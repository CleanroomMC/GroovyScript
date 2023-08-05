package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.CrusherRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Crusher extends VirtualizedRegistry<CrusherRecipe> {

    public Crusher() {
        super();
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> CrusherRecipe.recipeList.removeIf(r -> r == recipe));
        CrusherRecipe.recipeList.addAll(restoreFromBackup());
    }

    public void add(CrusherRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            CrusherRecipe.recipeList.add(recipe);
        }
    }

    public CrusherRecipe add(ItemStack output, IIngredient input, int energy) {
        CrusherRecipe recipe = new CrusherRecipe(output.copy(), ImmersiveEngineering.toIngredientStack(input), energy);
        add(recipe);
        return recipe;
    }

    public boolean remove(CrusherRecipe recipe) {
        if (CrusherRecipe.recipeList.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public void removeByOutput(ItemStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Immersive Engineering Crusher recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
        }
        List<CrusherRecipe> list = CrusherRecipe.removeRecipesForOutput(output);
        if (list.isEmpty()) {
            GroovyLog.msg("Error removing Immersive Engineering Crusher recipe")
                    .add("no recipes found for {}", output)
                    .error()
                    .post();
            return;
        }
        list.forEach(this::addBackup);
    }

    public void removeByInput(ItemStack input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Immersive Engineering Crusher recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
        }
        List<CrusherRecipe> list = CrusherRecipe.removeRecipesForInput(input);
        if (list.isEmpty()) {
            GroovyLog.msg("Error removing Immersive Engineering Crusher recipe")
                    .add("no recipes found for {}", input)
                    .error()
                    .post();
            return;
        }
        list.forEach(this::addBackup);
    }

    public SimpleObjectStream<CrusherRecipe> streamRecipes() {
        return new SimpleObjectStream<>(CrusherRecipe.recipeList).setRemover(this::remove);
    }

    public void removeAll() {
        CrusherRecipe.recipeList.forEach(this::addBackup);
        CrusherRecipe.recipeList.clear();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<CrusherRecipe> {

        private int energy;

        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Crusher recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            if (energy < 0) energy = 200;
        }

        @Override
        public @Nullable CrusherRecipe register() {
            if (!validate()) return null;
            CrusherRecipe recipe = new CrusherRecipe(output.get(0), input.get(0), energy);
            ModSupport.IMMERSIVE_ENGINEERING.get().crusher.add(recipe);
            return recipe;
        }
    }
}
