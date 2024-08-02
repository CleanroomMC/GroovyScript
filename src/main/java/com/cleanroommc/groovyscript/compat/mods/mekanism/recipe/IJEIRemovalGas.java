package com.cleanroommc.groovyscript.compat.mods.mekanism.recipe;

import com.cleanroommc.groovyscript.api.jeiremoval.IJEIRemoval;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.*;
import com.cleanroommc.groovyscript.compat.mods.mekanism.Mekanism;
import com.google.common.collect.ImmutableList;
import mekanism.client.jei.MekanismJEI;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IJEIRemovalGas extends IJEIRemoval.Default {

    static SlotOperation<?> getDefaultGas() {
        return new IngredientSlotOperation<>(
                MekanismJEI.TYPE_GAS, true,
                (stack, all) -> Mekanism.getSingleGasStack(stack, true));
    }

    @Override
    default @NotNull List<IOperation> getJEIOperations() {
        // TODO jei most mekanism classes can't do anything except "removeByInput", but with multiple parameters.
        return ImmutableList.of(ItemOperation.defaultOperation(), FluidOperation.defaultOperation(), getDefaultGas());
    }

}
