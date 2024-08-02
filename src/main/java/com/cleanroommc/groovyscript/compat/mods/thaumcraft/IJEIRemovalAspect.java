package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.buuz135.thaumicjei.ThaumcraftJEIPlugin;
import com.cleanroommc.groovyscript.api.jeiremoval.IJEIRemoval;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.*;
import com.google.common.collect.ImmutableList;
import net.minecraftforge.fml.common.Optional;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface IJEIRemovalAspect extends IJEIRemoval.Default {

    @Optional.Method(modid = "thaumicjei")
    static SlotOperation<?> getDefaultAspect() {
        return new IngredientSlotOperation<>(
                ThaumcraftJEIPlugin.ASPECT_LIST, false,
                (stack, all) -> stack.size() == 0 ? "" : Thaumcraft.asGroovyCode(stack.getAspects()[0], true));
    }

    @Override
    @Optional.Method(modid = "thaumicjei")
    default @NotNull List<IOperation> getJEIOperations() {
        return ImmutableList.of(ItemOperation.defaultOperation(), FluidOperation.defaultOperation(), getDefaultAspect());
    }

}
