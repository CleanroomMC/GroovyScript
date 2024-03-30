package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.core.inventory.ComparableItemStack;
import cofh.thermalexpansion.util.managers.dynamo.SteamManager;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = SteamManager.class, remap = false)
public interface SteamManagerAccessor {

    @Accessor
    static TObjectIntHashMap<ComparableItemStack> getFuelMap() {
        throw new UnsupportedOperationException();
    }

}
