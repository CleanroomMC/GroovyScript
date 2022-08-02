package com.cleanroommc.groovyscript.compat.mekanism;

import com.cleanroommc.groovyscript.api.IIngredient;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.machines.CrusherRecipe;
import net.minecraft.item.ItemStack;

public class Crusher {

    public void add(IIngredient ingredient, ItemStack output) {
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            RecipeHandler.Recipe.CRUSHER.put(new CrusherRecipe(itemStack, output));
        }
    }

    public void remove(ItemStack input, ItemStack output) {
        RecipeHandler.Recipe.CRUSHER.get().entrySet().removeIf(entry ->
                IngredientHelper.matches(entry.getValue().recipeInput, new IngredientWrapper((IIngredient) (Object) input)) &&
                        IngredientHelper.matches(entry.getValue().recipeOutput, new IngredientWrapper((IIngredient) (Object) output)));
    }
}
