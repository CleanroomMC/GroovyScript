package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.crafting.CokeOvenRecipe;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
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

    public CokeOvenRecipe add(ItemStack output, IIngredient input, int time, int creosoteOutput) {
        CokeOvenRecipe recipe = new CokeOvenRecipe(output.copy(), ImmersiveEngineering.toIEInput(input), time, creosoteOutput);
        add(recipe);
        return recipe;
    }

    public boolean remove(CokeOvenRecipe recipe) {
        if (CokeOvenRecipe.recipeList.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
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

    public SimpleObjectStream<CokeOvenRecipe> stream() {
        return new SimpleObjectStream<>(CokeOvenRecipe.recipeList).setRemover(this::remove);
    }

    public void removeAll() {
        CokeOvenRecipe.recipeList.forEach(this::addBackup);
        CokeOvenRecipe.recipeList.clear();
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
