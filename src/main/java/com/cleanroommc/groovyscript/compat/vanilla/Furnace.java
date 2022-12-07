package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.api.GroovyLog;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import java.util.ArrayList;
import java.util.List;

public class Furnace extends VirtualizedRegistry<Furnace.Recipe> {

    public Furnace() {
        super();
    }

    public void add(IIngredient input, ItemStack output) {
        add(input, output, 0.1f);
    }

    public void add(IIngredient input, ItemStack output, float exp) {
        if (GroovyLog.msg("Error adding Minecraft Furnace recipe")
                .add(IngredientHelper.isEmpty(input), () -> "Input must not be empty")
                .add(IngredientHelper.isEmpty(output), () -> "Output must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        if (exp < 0) {
            exp = 0.1f;
        }
        output = output.copy();
        for (ItemStack itemStack : input.getMatchingStacks()) {
            add(new Recipe(itemStack, output, exp));
        }
    }

    @GroovyBlacklist
    public void add(Recipe recipe) {
        FurnaceRecipes.instance().addSmeltingRecipe(recipe.input, recipe.output, recipe.exp);
        addScripted(recipe);
    }

    @GroovyBlacklist
    public boolean remove(Recipe recipe, boolean isScripted) {
        return removeByInput(recipe.input, isScripted);
    }

    @GroovyBlacklist
    private ItemStack findTrueInput(ItemStack input) {
        FurnaceRecipeAccess recipes = (FurnaceRecipeAccess) FurnaceRecipes.instance();
        ItemStack trueInput = recipes.getInputList().get(input);
        if (trueInput == null && input.getMetadata() != Short.MAX_VALUE) {
            input = new ItemStack(input.getItem(), input.getCount(), Short.MAX_VALUE);
            trueInput = recipes.getInputList().get(input);
        }
        return trueInput;
    }

    public boolean removeByInput(ItemStack input) {
        return removeByInput(input, true);
    }

    @GroovyBlacklist
    public boolean removeByInput(ItemStack input, boolean isScripted) {
        if (IngredientHelper.isEmpty(input)) {
            if (isScripted) {
                GroovyLog.msg("Error adding Minecraft Furnace recipe")
                        .add(IngredientHelper.isEmpty(input), () -> "Input must not be empty")
                        .error()
                        .postIfNotEmpty();
            }
            return false;
        }
        FurnaceRecipeAccess recipes = (FurnaceRecipeAccess) FurnaceRecipes.instance();

        ItemStack trueInput = findTrueInput(input);
        if (trueInput == null) {
            if (isScripted) {
                GroovyLog.msg("Error removing Minecraft Furnace recipe")
                        .add("Can't find recipe for input " + input)
                        .error()
                        .post();
            }
            return false;
        }
        ItemStack output = recipes.getSmeltingList().remove(trueInput);
        Float exp = recipes.getExperienceList().remove(output);

        if (output != null && exp != null) {
            Recipe recipe = new Recipe(trueInput, output, exp);
            if (isScripted) addBackup(recipe);
            return true;
        } else {
            if (isScripted) {
                GroovyLog.msg("Error removing Minecraft Furnace recipe")
                        .add(output == null, () -> "Found input, but no output for " + input)
                        .add(exp == null, () -> "Found input, but no exp value for " + input)
                        .error()
                        .post();
            }
        }

        return false;
    }

    public SimpleObjectStream<Recipe> streamRecipes() {
        FurnaceRecipeAccess furnaceRecipes = (FurnaceRecipeAccess) FurnaceRecipes.instance();
        List<Recipe> recipes = new ArrayList<>();
        for (ItemStack input : furnaceRecipes.getInputList().values()) {
            ItemStack output = furnaceRecipes.getSmeltingList().get(input);
            Float exp = furnaceRecipes.getExperienceList().get(output);
            if (output != null && exp != null) {
                recipes.add(new Recipe(input.copy(), output.copy(), exp));
            }
        }
        return new SimpleObjectStream<>(recipes, false).setRemover(recipe -> remove(recipe, true));
    }

    @GroovyBlacklist
    @Override
    public void onReload() {
        getScriptedRecipes().forEach(recipe -> remove(recipe, false));
        getBackupRecipes().forEach(recipe -> FurnaceRecipes.instance().addSmeltingRecipe(recipe.input, recipe.output, recipe.exp));
    }

    public static class Recipe {

        private final ItemStack input;
        private final ItemStack output;
        private final float exp;

        private Recipe(ItemStack input, ItemStack output, float exp) {
            this.input = input;
            this.output = output;
            this.exp = exp;
        }

        public ItemStack getInput() {
            return input.copy();
        }

        public ItemStack getOutput() {
            return output.copy();
        }

        public float getExp() {
            return exp;
        }
    }
}
