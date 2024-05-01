package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.thermalexpansion.util.managers.dynamo.MagmaticManager;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = MagmaticManager.class, remap = false)
public interface MagmaticManagerAccessor {

    @Accessor
    static Object2IntOpenHashMap<String> getFuelMap() {
        throw new UnsupportedOperationException();
    }

}
