package com.cleanroommc.groovyscript.helper.recipe;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;

import java.util.ArrayList;
import java.util.List;

public class CraftingRecipeHelper {

    public void addShaped(String name, ItemStack output, List<List<IIngredient>> input) {
        List<IIngredient> inputs = new ArrayList<>();
        int w = 0;
        for (List<IIngredient> row : input) {
            if (w != 0 && w != row.size()) {
                throw new IllegalArgumentException("All rows must be the same size");
            }
            w = row.size();
            inputs.addAll(row);
        }
        ShapedCraftingRecipe recipe = new ShapedCraftingRecipe(output.createMcItemStack(), inputs, w, input.size());
        recipe.setRegistryName(name);
        ReloadableRegistryManager.registerRecipe(recipe);
    }

    public void remove(String name) {
        ReloadableRegistryManager.removeRecipe(name);
    }
}
