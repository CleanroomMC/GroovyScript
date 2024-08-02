package com.cleanroommc.groovyscript.compat.mods.betterwithmods;

import betterwithmods.api.recipe.IOutput;
import com.cleanroommc.groovyscript.api.jeiremoval.IJEIRemoval;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.*;
import com.cleanroommc.groovyscript.helper.ingredient.GroovyScriptCodeConverter;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IJEIRemovalIOutput extends IJEIRemoval.Default {

    static SlotOperation<IOutput> getDefaultIOutput() {
        return new ClassSlotOperation<>(
                IOutput.class, true,
                (stack, all) -> GroovyScriptCodeConverter.getSingleItemStack(stack.getOutput(), true, false));
    }

    @Override
    default @NotNull List<IOperation> getJEIOperations() {
        return ImmutableList.of(ItemOperation.defaultOperation(), FluidOperation.defaultOperation(), getDefaultIOutput());
    }

}
