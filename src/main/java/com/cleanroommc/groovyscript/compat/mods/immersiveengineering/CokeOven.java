package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.CokeOvenRecipe;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.RecipeStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class CokeOven extends VirtualizedRegistry<CokeOvenRecipe> {

    public CokeOven() {
        super("CokeOven", "cokeoven");
    }

    public static RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> CokeOvenRecipe.recipeList.removeIf(r -> r == recipe));
        CokeOvenRecipe.recipeList.addAll(restoreFromBackup());
    }

    public void add(CokeOvenRecipe recipe) {
        if (recipe != null) {
            CokeOvenRecipe.recipeList.add(recipe);
            addScripted(recipe);
        }
    }

    public CokeOvenRecipe add(ItemStack output, Object input, int time, int creosoteOutput) {
        CokeOvenRecipe recipe = create(output, input, time, creosoteOutput);
        addScripted(recipe);
        return recipe;
    }

    public void remove(CokeOvenRecipe recipe) {
        if (CokeOvenRecipe.recipeList.removeIf(r -> r == recipe)) addBackup(recipe);
    }

    public void removeByOutput(ItemStack output) {
        List<CokeOvenRecipe> list = CokeOvenRecipe.removeRecipes(output);
        if (list.size() > 0) list.forEach(this::addBackup);
    }

    public void removeByInput(ItemStack input) {
        for (int i = 0; i < CokeOvenRecipe.recipeList.size(); i++) {
            CokeOvenRecipe recipe = CokeOvenRecipe.recipeList.get(i);
            if (ApiUtils.stackMatchesObject(input, recipe.input)) {
                addBackup(recipe);
                CokeOvenRecipe.recipeList.remove(i);
                break;
            }
        }
    }

    public RecipeStream<CokeOvenRecipe> stream() {
        return new RecipeStream<>(CokeOvenRecipe.recipeList).setRemover(recipe -> {
            CokeOvenRecipe recipe1 = CokeOvenRecipe.findRecipe(ApiUtils.getItemStackFromObject(recipe.input));
            if (recipe1 != null) {
                remove(recipe1);
                return true;
            }
            return false;
        });
    }

    public void removeAll() {
        CokeOvenRecipe.recipeList.forEach(this::addBackup);
        CokeOvenRecipe.recipeList.clear();
    }

    private static CokeOvenRecipe create(ItemStack output, Object input, int time, int creosoteOutput) {
        if (!(input instanceof ItemStack)) {
            if (input instanceof IIngredient) input = Arrays.asList(((IIngredient) input).getMatchingStacks());
        }

        return new CokeOvenRecipe(output, input, time, creosoteOutput);
    }

    public static class RecipeBuilder extends TimeRecipeBuilder<CokeOvenRecipe> {

        protected int creosote;

        public RecipeBuilder creosote(int creosote) {
            this.creosote = creosote;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Coke Oven recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            if (time < 0) time = 200;
            if (creosote < 0) creosote = 0;
        }

        @Override
        public @Nullable CokeOvenRecipe register() {
            if (!validate()) return null;
            return ModSupport.IMMERSIVE_ENGINEERING.get().cokeOven.add(output.get(0), input.get(0), time, creosote);
        }
    }
}
