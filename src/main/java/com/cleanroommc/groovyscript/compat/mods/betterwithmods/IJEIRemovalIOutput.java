package com.cleanroommc.groovyscript.compat.mods.betterwithmods;

import betterwithmods.api.recipe.IOutput;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.IJEIRemoval;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.OperationHandler;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IJEIRemovalIOutput extends IJEIRemoval.Default {

    static OperationHandler.SlotOperation<IOutput> getDefaultIOutput() {
        return new OperationHandler.ClassSlotOperation<>(
                IOutput.class, true,
                (stack, all) -> GroovyScriptCodeConverter.getSingleItemStack(stack.getOutput(), false, false));
    }

    @Override
    default @NotNull List<OperationHandler.IOperation> getJEIOperations() {
        return ImmutableList.of(OperationHandler.ItemOperation.defaultItemOperation(), OperationHandler.FluidOperation.defaultFluidOperation(), getDefaultIOutput());
    }

}
