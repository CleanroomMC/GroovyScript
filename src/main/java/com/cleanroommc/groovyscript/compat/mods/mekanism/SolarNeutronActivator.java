package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import mekanism.api.gas.GasStack;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.GasInput;
import mekanism.common.recipe.machines.SolarNeutronRecipe;

public class SolarNeutronActivator extends VirtualizedMekanismRegistry<SolarNeutronRecipe> {

    public SolarNeutronActivator() {
        super(RecipeHandler.Recipe.SOLAR_NEUTRON_ACTIVATOR, "SolarNeutronActivator", "solar_neutron_activator", "SNA");
    }

    public SolarNeutronRecipe add(GasStack input, GasStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Solar Neutron Activator recipe").error();
        msg.add(IngredientHelper.isEmpty(input), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(output), () -> "output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        SolarNeutronRecipe recipe = new SolarNeutronRecipe(input.copy(), output.copy());
        recipeRegistry.put(recipe);
        addScripted(recipe);
        return recipe;
    }

    public boolean removeByInput(GasStack input) {
        GroovyLog.Msg msg = GroovyLog.msg("Error removing Mekanism Solar Neutron Activator recipe").error();
        msg.add(IngredientHelper.isEmpty(input), () -> "input must not be empty");
        if (msg.postIfNotEmpty()) return false;

        SolarNeutronRecipe recipe = recipeRegistry.get().remove(new GasInput(input));
        if (recipe != null) {
            addBackup(recipe);
            return true;
        }
        removeError("could not find recipe for %", input);
        return false;
    }
}
