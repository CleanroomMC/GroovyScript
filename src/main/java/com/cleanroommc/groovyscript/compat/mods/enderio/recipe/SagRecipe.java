package com.cleanroommc.groovyscript.compat.mods.enderio.recipe;

import com.enderio.core.common.util.NNList;
import crazypants.enderio.base.recipe.*;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class SagRecipe extends Recipe {

    public SagRecipe(IRecipeInput input, int energyRequired, RecipeBonusType bonusType, RecipeLevel level, RecipeOutput... output) {
        super(input, energyRequired, bonusType, level, output);
    }

    @Override
    public boolean isInputForRecipe(NNList<MachineRecipeInput> machineInputs) {
        return machineInputs.size() == 1 && getInputs()[0].isInput(machineInputs.get(0).item);
    }

    @Override
    public boolean isValidInput(@NotNull FluidStack fluid) {
        return false;
    }

}
