package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.GasRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import mekanism.api.gas.GasStack;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.ChemicalPairInput;
import mekanism.common.recipe.machines.ChemicalInfuserRecipe;
import org.jetbrains.annotations.Nullable;

public class ChemicalInfuser extends VirtualizedMekanismRegistry<ChemicalInfuserRecipe> {

    public ChemicalInfuser() {
        super(RecipeHandler.Recipe.CHEMICAL_INFUSER);
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public ChemicalInfuserRecipe add(GasStack leftInput, GasStack rightInput, GasStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Chemical Infuser recipe").error();
        msg.add(Mekanism.isEmpty(leftInput), () -> "left gas input must not be empty");
        msg.add(Mekanism.isEmpty(rightInput), () -> "right gas input must not be empty");
        msg.add(Mekanism.isEmpty(output), () -> "gas output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        ChemicalInfuserRecipe recipe = new ChemicalInfuserRecipe(leftInput.copy(), rightInput.copy(), output.copy());
        addScripted(recipe);
        recipeRegistry.put(recipe);
        return recipe;
    }

    public boolean removeByInput(GasStack leftInput, GasStack rightInput) {
        GroovyLog.Msg msg = GroovyLog.msg("Error removing Mekanism Chemical Infuser recipe").error();
        msg.add(Mekanism.isEmpty(leftInput), () -> "left gas input must not be empty");
        msg.add(Mekanism.isEmpty(rightInput), () -> "right gas input must not be empty");
        if (msg.postIfNotEmpty()) return false;

        ChemicalInfuserRecipe recipe = recipeRegistry.get().remove(new ChemicalPairInput(leftInput, rightInput));
        if (recipe != null) {
            addBackup(recipe);
            return true;
        }
        removeError("could not find recipe for {} and {}", leftInput, rightInput);
        return false;
    }

    public static class RecipeBuilder extends GasRecipeBuilder<ChemicalInfuserRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Chemical Infuser recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg);
            validateGases(msg, 2, 2, 1, 1);
        }

        @Override
        public @Nullable ChemicalInfuserRecipe register() {
            if (!validate()) return null;
            ChemicalInfuserRecipe recipe = new ChemicalInfuserRecipe(gasInput.get(0), gasInput.get(1), gasOutput.get(0));
            ModSupport.MEKANISM.get().chemicalInfuser.add(recipe);
            return recipe;
        }
    }
}
