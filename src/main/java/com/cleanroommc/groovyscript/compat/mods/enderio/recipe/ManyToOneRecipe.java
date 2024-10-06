package com.cleanroommc.groovyscript.compat.mods.enderio.recipe;

import com.enderio.core.common.util.NNList;
import crazypants.enderio.base.recipe.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManyToOneRecipe extends Recipe {

    public ManyToOneRecipe(RecipeOutput output, int energyRequired, RecipeBonusType bonusType, RecipeLevel level, IRecipeInput... input) {
        super(output, energyRequired, bonusType, level, input);
    }

    public RecipeOutput getOutput() {
        return getOutputs()[0];
    }

    @Override
    public boolean isInputForRecipe(NNList<MachineRecipeInput> machineInputs) {
        if (getInputs().length != machineInputs.size()) return false;

        List<IRecipeInput> inputs = new ArrayList<>();
        Collections.addAll(inputs, getInputs());
        for (MachineRecipeInput input : machineInputs) {
            if (inputs.isEmpty()) return false;
            inputs.removeIf(iRecipeInput -> (iRecipeInput.getSlotNumber() < 0 || iRecipeInput.getSlotNumber() == input.slotNumber) && iRecipeInput.isInput(input.item));
        }
        return inputs.isEmpty();
    }

    @Override
    public boolean isValidInput(@NotNull FluidStack fluid) {
        return false;
    }

    @Override
    public boolean isValidInput(int slot, @NotNull ItemStack item) {
        IRecipeInput input = getInputs()[slot];
        if ((input.getSlotNumber() < 0 || input.getSlotNumber() == slot) && input.isInput(item)) {
            return true;
        }
        for (IRecipeInput input1 : getInputs()) {
            if ((input1.getSlotNumber() < 0 || input1.getSlotNumber() == slot) && input1.isInput(item)) {
                return true;
            }
        }
        return false;
    }
}
