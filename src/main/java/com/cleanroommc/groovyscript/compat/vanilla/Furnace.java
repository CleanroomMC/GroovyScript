package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Furnace extends VirtualizedRegistry<Furnace.Recipe> {

    public Furnace() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
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
        return removeByInput(recipe.input, isScripted, isScripted);
    }

    @GroovyBlacklist
    private ItemStack findTrueInput(ItemStack input) {
        ItemStack trueInput = FurnaceRecipeManager.inputMap.get(input);
        if (trueInput == null && input.getMetadata() != Short.MAX_VALUE) {
            input = new ItemStack(input.getItem(), input.getCount(), Short.MAX_VALUE);
            trueInput = FurnaceRecipeManager.inputMap.get(input);
        }
        return trueInput;
    }

    public boolean removeByInput(ItemStack input) {
        return removeByInput(input, true);
    }

    public boolean removeByInput(ItemStack input, boolean log) {
        return removeByInput(input, log, true);
    }

    @GroovyBlacklist
    public boolean removeByInput(ItemStack input, boolean log, boolean isScripted) {
        if (IngredientHelper.isEmpty(input)) {
            if (log) {
                GroovyLog.msg("Error adding Minecraft Furnace recipe")
                        .add(IngredientHelper.isEmpty(input), () -> "Input must not be empty")
                        .error()
                        .postIfNotEmpty();
            }
            return false;
        }

        ItemStack trueInput = findTrueInput(input);
        if (trueInput == null) {
            if (log) {
                GroovyLog.msg("Error removing Minecraft Furnace recipe")
                        .add("Can't find recipe for input " + input)
                        .error()
                        .post();
            }
            return false;
        }
        ItemStack output = FurnaceRecipes.instance().getSmeltingList().remove(trueInput);
        if (output != null) {
            float exp = FurnaceRecipes.instance().getSmeltingExperience(output);
            Recipe recipe = new Recipe(trueInput, output, exp);
            if (isScripted) addBackup(recipe);
            return true;
        } else {
            if (log) {
                GroovyLog.msg("Error removing Minecraft Furnace recipe")
                        .add("Found input, but no output for " + input)
                        .error()
                        .post();
            }
        }

        return false;
    }

    public boolean removeByOutput(IIngredient output) {
        return removeByOutput(output, true);
    }

    public boolean removeByOutput(IIngredient output, boolean log) {
        return removeByOutput(output, log, true);
    }

    @GroovyBlacklist
    public boolean removeByOutput(IIngredient output, boolean log, boolean isScripted) {
        if (IngredientHelper.isEmpty(output)) {
            if (log) {
                GroovyLog.msg("Error adding Minecraft Furnace recipe")
                        .add(IngredientHelper.isEmpty(output), () -> "Output must not be empty")
                        .error()
                        .postIfNotEmpty();
            }
            return false;
        }

        List<Recipe> recipesToRemove = new ArrayList<>();
        for (Map.Entry<ItemStack, ItemStack> entry : FurnaceRecipes.instance().getSmeltingList().entrySet()) {
            if (output.test(entry.getValue())) {
                float exp = FurnaceRecipes.instance().getSmeltingExperience(entry.getValue());
                Recipe recipe = new Recipe(entry.getKey(), entry.getValue(), exp);
                recipesToRemove.add(recipe);
            }
        }
        if (recipesToRemove.isEmpty()) {
            if (log) {
                GroovyLog.msg("Error removing Minecraft Furnace recipe")
                        .add("Can't find recipe for output " + output)
                        .error()
                        .post();
            }
            return false;
        }

        for (Recipe recipe : recipesToRemove) {
            if (isScripted) addBackup(recipe);
            FurnaceRecipes.instance().getSmeltingList().remove(recipe.input);
        }

        return true;
    }

    public SimpleObjectStream<Recipe> streamRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        for (Map.Entry<ItemStack, ItemStack> entry : FurnaceRecipes.instance().getSmeltingList().entrySet()) {
            float exp = FurnaceRecipes.instance().getSmeltingExperience(entry.getValue());
            recipes.add(new Recipe(entry.getKey(), entry.getValue(), exp));
        }
        return new SimpleObjectStream<>(recipes, false).setRemover(recipe -> remove(recipe, true));
    }

    public void removeAll() {
        FurnaceRecipes.instance().getSmeltingList().entrySet().removeIf(entry -> {
            float exp = FurnaceRecipes.instance().getSmeltingExperience(entry.getValue());
            Recipe recipe = new Recipe(entry.getKey(), entry.getValue(), exp);
            addBackup(recipe);
            return true;
        });
    }

    @GroovyBlacklist
    @Override
    public void onReload() {
        getScriptedRecipes().forEach(recipe -> remove(recipe, false));
        getBackupRecipes().forEach(recipe -> FurnaceRecipes.instance().addSmeltingRecipe(recipe.input, recipe.output, recipe.exp));
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<Recipe> {

        private IIngredient input;
        private ItemStack output;
        private float exp = 0.1f;

        public RecipeBuilder input(IIngredient input) {
            this.input = input;
            return this;
        }

        public RecipeBuilder output(ItemStack output) {
            this.output = output;
            return this;
        }

        public RecipeBuilder exp(float exp) {
            this.exp = exp;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Minecraft Furnace recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            msg.add(IngredientHelper.isEmpty(input), () -> "Input must not be empty");
            msg.add(IngredientHelper.isEmpty(output), () -> "Output must not be empty");
            if (exp < 0) {
                exp = 0.1f;
            }
        }

        @Override
        public @Nullable Recipe register() {
            if (!validate()) return null;
            Recipe recipe = null;
            for (ItemStack itemStack : input.getMatchingStacks()) {
                recipe = new Recipe(itemStack, output, exp);
                VanillaModule.furnace.add(recipe);
            }
            return recipe;
        }
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
