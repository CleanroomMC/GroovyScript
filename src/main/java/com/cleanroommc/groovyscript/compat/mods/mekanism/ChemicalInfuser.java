package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import mekanism.api.gas.GasStack;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.ChemicalPairInput;
import mekanism.common.recipe.machines.ChemicalInfuserRecipe;

public class ChemicalInfuser extends VirtualizedMekanismRegistry<ChemicalInfuserRecipe> {

    public ChemicalInfuser() {
        super(RecipeHandler.Recipe.CHEMICAL_INFUSER, "ChemicalInfuser", "chemical_infuser");
    }

    public ChemicalInfuserRecipe add(GasStack leftInput, GasStack rightInput, GasStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Chemical Infuser recipe").error();
        msg.add(IngredientHelper.isEmpty(leftInput), () -> "left gas input must not be empty");
        msg.add(IngredientHelper.isEmpty(rightInput), () -> "right gas input must not be empty");
        msg.add(IngredientHelper.isEmpty(output), () -> "gas output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        ChemicalInfuserRecipe recipe = new ChemicalInfuserRecipe(leftInput.copy(), rightInput.copy(), output.copy());
        recipeRegistry.put(recipe);
        return recipe;
    }

    public boolean removeByInput(GasStack leftInput, GasStack rightInput) {
        GroovyLog.Msg msg = GroovyLog.msg("Error removing Mekanism Chemical Infuser recipe").error();
        msg.add(IngredientHelper.isEmpty(leftInput), () -> "left gas input must not be empty");
        msg.add(IngredientHelper.isEmpty(rightInput), () -> "right gas input must not be empty");
        if (msg.postIfNotEmpty()) return false;

        ChemicalInfuserRecipe recipe = recipeRegistry.get().remove(new ChemicalPairInput(leftInput, rightInput));
        if (recipe != null) {
            addBackup(recipe);
            return true;
        }
        removeError("could not find recipe for %s and %s", leftInput, rightInput);
        return false;
    }
}
