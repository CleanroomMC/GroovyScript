package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import mekanism.api.gas.GasStack;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.FluidInput;
import mekanism.common.recipe.machines.SeparatorRecipe;
import net.minecraftforge.fluids.FluidStack;

public class Separator extends VirtualizedMekanismRegistry<SeparatorRecipe> {

    public Separator() {
        super(RecipeHandler.Recipe.ELECTROLYTIC_SEPARATOR, VirtualizedRegistry.generateAliases("ElectrolyticSeparator"));
    }

    public SeparatorRecipe add(FluidStack input, GasStack leftOutput, GasStack rightOutput, double energy) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Electrolytic Separator recipe").error();
        msg.add(IngredientHelper.isEmpty(input), () -> "input must not be empty");
        msg.add(Mekanism.isEmpty(leftOutput), () -> "left gas output must not be empty");
        msg.add(Mekanism.isEmpty(rightOutput), () -> "right gas output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        SeparatorRecipe recipe = new SeparatorRecipe(input.copy(), energy, leftOutput.copy(), rightOutput.copy());
        recipeRegistry.put(recipe);
        return recipe;
    }

    public boolean removeByInput(FluidStack input) {
        GroovyLog.Msg msg = GroovyLog.msg("Error removing Mekanism Electrolytic Separator recipe").error();
        msg.add(IngredientHelper.isEmpty(input), () -> "input must not be empty");
        if (msg.postIfNotEmpty()) return false;

        SeparatorRecipe recipe = recipeRegistry.get().remove(new FluidInput(input));
        if (recipe != null) {
            addBackup(recipe);
            return true;
        }
        removeError("could not find recipe for %s", input);
        return false;
    }
}
