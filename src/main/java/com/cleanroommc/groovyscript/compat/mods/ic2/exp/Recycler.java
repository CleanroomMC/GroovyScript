package com.cleanroommc.groovyscript.compat.mods.ic2.exp;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ic2.RecipeInput;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.MachineRecipe;
import ic2.api.recipe.Recipes;

import java.util.Iterator;

public class Recycler extends VirtualizedRegistry<IRecipeInput> {

    private int type = 0; // 0 = Whitelist | 1 = Blacklist

    private static final Object dummy = new Object();

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> {
            if (type == 0) {
                for (Iterator<IRecipeInput> iterator = Recipes.recyclerWhitelist.iterator(); iterator.hasNext(); ) {
                    IRecipeInput input = iterator.next();
                    if (input == recipe) iterator.remove();
                }
            } else {
                for (Iterator<IRecipeInput> iterator = Recipes.recyclerBlacklist.iterator(); iterator.hasNext(); ) {
                    IRecipeInput input = iterator.next();
                    if (input == recipe) iterator.remove();
                }
            }
        });
    }

    public void addBlacklist(IIngredient ingredient) {
        if (GroovyLog.msg("Error setting Recycler recipe")
                .add(IngredientHelper.isEmpty(ingredient), () -> "ingredient must not be empty")
                .add(!Recipes.recyclerWhitelist.isEmpty(), () -> "whitelist should be empty to set blacklist for Recycler")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        this.type = 1;
        IRecipeInput input = new RecipeInput(ingredient);
        Recipes.recyclerBlacklist.add(input);
        addScripted(input);
    }

    public void addWhitelist(IIngredient ingredient) {
        if (GroovyLog.msg("Error setting Recycler recipe")
                .add(IngredientHelper.isEmpty(ingredient), () -> "ingredient must not be empty")
                .add(!Recipes.recyclerBlacklist.isEmpty(), () -> "blacklist should be empty to set whitelist for Recycler")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        this.type = 0;
        IRecipeInput input = new RecipeInput(ingredient);
        Recipes.recyclerWhitelist.add(input);
        addScripted(input);
    }

    private void addBlacklist(MachineRecipe<IRecipeInput, Object> recipe) {
        this.type = 1;
        Recipes.recyclerBlacklist.add(recipe.getInput());
        addScripted(recipe.getInput());
    }

    private void addWhitelist(MachineRecipe<IRecipeInput, Object> recipe) {
        this.type = 0;
        Recipes.recyclerWhitelist.add(recipe.getInput());
        addScripted(recipe.getInput());
    }
}
