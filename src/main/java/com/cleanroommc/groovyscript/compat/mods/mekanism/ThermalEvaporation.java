package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.api.GroovyLog;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.FluidInput;
import mekanism.common.recipe.machines.ThermalEvaporationRecipe;
import net.minecraftforge.fluids.FluidStack;

public class ThermalEvaporation extends VirtualizedMekanismRegistry<ThermalEvaporationRecipe> {

    public ThermalEvaporation() {
        super(RecipeHandler.Recipe.THERMAL_EVAPORATION_PLANT, "TEP");
    }

    public ThermalEvaporationRecipe add(FluidStack input, FluidStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Solar Neutron Activator recipe").error();
        msg.add(IngredientHelper.isEmpty(input), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(output), () -> "output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        ThermalEvaporationRecipe recipe = new ThermalEvaporationRecipe(input.copy(), output.copy());
        recipeRegistry.put(recipe);
        addScripted(recipe);
        return recipe;
    }

    public boolean removeByInput(FluidStack input) {
        GroovyLog.Msg msg = GroovyLog.msg("Error removing Mekanism Solar Neutron Activator recipe").error();
        msg.add(IngredientHelper.isEmpty(input), () -> "input must not be empty");
        if (msg.postIfNotEmpty()) return false;

        ThermalEvaporationRecipe recipe = recipeRegistry.get().remove(new FluidInput(input));
        if (recipe != null) {
            addBackup(recipe);
            return true;
        }
        removeError("could not find recipe for %", input);
        return false;
    }
}
