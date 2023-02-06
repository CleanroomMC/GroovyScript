package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.api.GroovyLog;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.SawmillRecipe;
import mekanism.common.recipe.outputs.ChanceOutput;
import net.minecraft.item.ItemStack;

public class Sawmill extends VirtualizedMekanismRegistry<SawmillRecipe> {

    public Sawmill() {
        super(RecipeHandler.Recipe.PRECISION_SAWMILL);
    }

    public SawmillRecipe add(IIngredient ingredient, ItemStack output) {
        return add(ingredient, output, null, 0.0);
    }

    public SawmillRecipe add(IIngredient ingredient, ItemStack output, ItemStack secondary) {
        return add(ingredient, output, secondary, 1.0);
    }

    public SawmillRecipe add(IIngredient ingredient, ItemStack output, ItemStack secondary, double chance) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Sawmill recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(output), () -> "output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        boolean withSecondary = !IngredientHelper.isEmpty(secondary);
        if (withSecondary) {
            if (chance <= 0) chance = 1;
            secondary = secondary.copy();
        }

        output = output.copy();
        SawmillRecipe recipe1 = null;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            SawmillRecipe recipe;
            ChanceOutput chanceOutput = withSecondary ? new ChanceOutput(output, secondary, chance) : new ChanceOutput(output);
            recipe = new SawmillRecipe(new ItemStackInput(itemStack.copy()), chanceOutput);
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
