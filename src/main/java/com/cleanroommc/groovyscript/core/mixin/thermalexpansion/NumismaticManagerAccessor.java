package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.core.inventory.ComparableItemStack;
import cofh.thermalexpansion.util.managers.dynamo.NumismaticManager;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = NumismaticManager.class, remap = false)
public interface NumismaticManagerAccessor {

    @Accessor
    static TObjectIntHashMap<ComparableItemStack> getFuelMap() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static TObjectIntHashMap<ComparableItemStack> getGemFuelMap() {
        throw new UnsupportedOperationException();
    }

}
