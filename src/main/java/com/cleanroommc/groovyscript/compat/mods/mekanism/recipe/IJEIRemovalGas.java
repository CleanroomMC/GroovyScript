package com.cleanroommc.groovyscript.compat.mods.mekanism.recipe;

import com.cleanroommc.groovyscript.compat.mods.jei.removal.IJEIRemoval;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.OperationHandler;
import com.cleanroommc.groovyscript.compat.mods.mekanism.Mekanism;
import com.google.common.collect.ImmutableList;
import mekanism.client.jei.MekanismJEI;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IJEIRemovalGas extends IJEIRemoval.Default {

    static OperationHandler.SlotOperation<?> getDefaultGas() {
        return new OperationHandler.IngredientSlotOperation<>(
                MekanismJEI.TYPE_GAS, true,
                (stack, all) -> Mekanism.getSingleGasStack(stack, true));
    }

    @Override
    default @NotNull List<OperationHandler.IOperation> getJEIOperations() {
        return ImmutableList.of(OperationHandler.ItemOperation.defaultItemOperation(), OperationHandler.FluidOperation.defaultFluidOperation(), getDefaultGas());
    }

}
