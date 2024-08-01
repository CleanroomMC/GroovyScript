package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.buuz135.thaumicjei.ThaumcraftJEIPlugin;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.IJEIRemoval;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.OperationHandler;
import com.google.common.collect.ImmutableList;
import net.minecraftforge.fml.common.Optional;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IJEIRemovalAspect extends IJEIRemoval.Default {

    @Optional.Method(modid = "thaumicjei")
    static OperationHandler.SlotOperation<?> getDefaultAspect() {
        return new OperationHandler.IngredientSlotOperation<>(
                ThaumcraftJEIPlugin.ASPECT_LIST, false,
                (stack, all) -> stack.size() == 0 ? "" : Thaumcraft.asGroovyCode(stack.getAspects()[0], false));
    }

    @Override
    @Optional.Method(modid = "thaumicjei")
    default @NotNull List<OperationHandler.IOperation> getJEIOperations() {
        return ImmutableList.of(OperationHandler.ItemOperation.defaultItemOperation(), OperationHandler.FluidOperation.defaultFluidOperation(), getDefaultAspect());
    }

}
