package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.CrusherRecipe;
import mekanism.common.recipe.machines.SawmillRecipe;
import net.minecraft.item.ItemStack;

public class Sawmill extends VirtualizedMekanismRegistry<SawmillRecipe> {

    public Sawmill() {
        super(RecipeHandler.Recipe.PRECISION_SAWMILL, "Sawmill", "sawmill");
    }

    public SawmillRecipe add(IIngredient ingredient, ItemStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Sawmill recipe");
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(output), () -> "output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        output = output.copy();
        SawmillRecipe recipe1 = null;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            SawmillRecipe recipe = new SawmillRecipe(itemStack.copy(), output);
            if (recipe1 == null) recipe1 = recipe;
            recipeRegistry.put(recipe);
            addScripted(recipe);
        }
        return recipe1;
    }

    public boolean removeByInput(IIngredient ingredient) {
        if (IngredientHelper.isEmpty(ingredient)) {
            removeError("input must not be empty");
            return false;
        }
        boolean found = false;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            SawmillRecipe recipe = recipeRegistry.get().remove(new ItemStackInput(itemStack));
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
