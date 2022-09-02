package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.CrusherRecipe;
import net.minecraft.item.ItemStack;

public class Crusher extends VirtualizedMekanismRegistry<CrusherRecipe> {

    public Crusher() {
        super(RecipeHandler.Recipe.CRUSHER, "Crusher", "crusher");
    }

    public void add(IIngredient ingredient, ItemStack output) {
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            CrusherRecipe recipe = new CrusherRecipe(itemStack, output);
            RecipeHandler.Recipe.CRUSHER.put(recipe);
            addScripted(recipe);
        }
    }

    public boolean removeByInput(IIngredient ingredient) {
        if (IngredientHelper.isEmpty(ingredient)) {
            removeError("input must not be empty");
            return false;
        }
        boolean found = false;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            CrusherRecipe recipe = recipeRegistry.get().remove(new ItemStackInput(itemStack));
            if (recipe != null) {
                addBackup(recipe);
                found = true;
            }
        }
        if (!found) {
            removeError("could not find recipe for %s", ingredient);
        }
        return found;
    }
}
