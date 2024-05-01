package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.core.inventory.ComparableItemStack;
import cofh.thermalexpansion.util.managers.device.DiffuserManager;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = DiffuserManager.class, remap = false)
public interface DiffuserManagerAccessor {

    @Accessor
    static TObjectIntHashMap<ComparableItemStack> getReagentAmpMap() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static TObjectIntHashMap<ComparableItemStack> getReagentDurMap() {
        throw new UnsupportedOperationException();
    }

}
