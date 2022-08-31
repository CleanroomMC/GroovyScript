package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.AlloyRecipe;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.RecipeStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AlloyKiln extends VirtualizedRegistry<AlloyRecipe> {
    public AlloyKiln() {
        super("AlloyKiln", "alloykiln");
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> AlloyRecipe.recipeList.removeIf(r -> r == recipe));
        AlloyRecipe.recipeList.addAll(restoreFromBackup());
    }

    public void add(AlloyRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            AlloyRecipe.recipeList.add(recipe);
        }
    }

    public AlloyRecipe add(ItemStack output, Object input0, Object input1, int time) {
        AlloyRecipe recipe = create(output, input0, input1, time);
        add(recipe);
        return recipe;
    }

    public void remove(AlloyRecipe recipe) {
        if (AlloyRecipe.recipeList.removeIf(r -> r == recipe)) addBackup(recipe);
    }

    public void removeByOutput(ItemStack output) {
        List<AlloyRecipe> recipes = AlloyRecipe.removeRecipes(output);
        if (recipes.size() > 0) recipes.forEach(this::addBackup);
    }

    public void removeByInput(ItemStack input, ItemStack input1) {
        AlloyRecipe recipe = AlloyRecipe.findRecipe(input, input1);
        if (recipe != null) {
            remove(recipe);
        }
    }

    public RecipeStream<AlloyRecipe> stream() {
        return new RecipeStream<>(AlloyRecipe.recipeList).setRemover(recipe -> {
            AlloyRecipe recipe1 = AlloyRecipe.findRecipe(recipe.input0.stack, recipe.input1.stack);
            if (recipe1 != null) {
                remove(recipe1);
                addBackup(recipe1);
                return true;
            }
            return false;
        });
    }

    public void removeAll() {
        AlloyRecipe.recipeList.forEach(this::addBackup);
        AlloyRecipe.recipeList.clear();
    }

    private static AlloyRecipe create(ItemStack output, Object input0, Object input1, int time) {
        if (input0 instanceof IIngredient) input0 = ((IIngredient) input0).getMatchingStacks();
        if (input1 instanceof IIngredient) input1 = ((IIngredient) input1).getMatchingStacks();

        return new AlloyRecipe(output, input0, input1, time);
    }

    public static class RecipeBuilder extends TimeRecipeBuilder<AlloyRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Alloy Kiln recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 2, 2, 1, 1);
            validateFluids(msg);
            if (time < 0) time = 200;
        }

        @Override
        public @Nullable AlloyRecipe register() {
            if (!validate()) return null;
            return ModSupport.IMMERSIVE_ENGINEERING.get().alloyKiln.add(output.get(0), input.get(0), input.get(1), time);
        }
    }

}
