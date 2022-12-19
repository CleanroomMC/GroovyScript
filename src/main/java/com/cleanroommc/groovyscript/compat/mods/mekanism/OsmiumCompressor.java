package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.api.GroovyLog;
import mekanism.api.gas.GasStack;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.AdvancedMachineInput;
import mekanism.common.recipe.machines.OsmiumCompressorRecipe;
import mekanism.common.recipe.outputs.ItemStackOutput;
import net.minecraft.item.ItemStack;

public class OsmiumCompressor extends VirtualizedMekanismRegistry<OsmiumCompressorRecipe> {

    public OsmiumCompressor() {
        super(RecipeHandler.Recipe.OSMIUM_COMPRESSOR);
    }

    public OsmiumCompressorRecipe add(IIngredient ingredient, GasStack gasInput, ItemStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Osmium Compressor recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(output), () -> "output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        output = output.copy();
        OsmiumCompressorRecipe recipe1 = null;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            OsmiumCompressorRecipe recipe = new OsmiumCompressorRecipe(new AdvancedMachineInput(itemStack.copy(), gasInput.getGas()), new ItemStackOutput(output));
            if (recipe1 == null) recipe1 = recipe;
            recipeRegistry.put(recipe);
            addScripted(recipe);
        }
        return recipe1;
    }

    public boolean removeByInput(IIngredient ingredient, GasStack gasInput) {
        GroovyLog.Msg msg = GroovyLog.msg("Error removing Mekanism Osmium Compressor recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(Mekanism.isEmpty(gasInput), () -> "gas input must not be empty");
        if (msg.postIfNotEmpty()) return false;

        boolean found = false;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            OsmiumCompressorRecipe recipe = recipeRegistry.get().remove(new AdvancedMachineInput(itemStack, gasInput.getGas()));
            if (recipe != null) {
                addBackup(recipe);
                found = true;
            }
        }
        if (!found) {
            removeError("could not find recipe for %s and %s", ingredient, gasInput);
        }
        return found;
    }
}
