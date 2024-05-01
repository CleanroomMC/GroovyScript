package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.thermalexpansion.util.managers.device.CoolantManager;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = CoolantManager.class, remap = false)
public interface CoolantManagerAccessor {

    @Accessor
    static Object2IntOpenHashMap<String> getCoolantMap() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Object2IntOpenHashMap<String> getCoolantFactorMap() {
        throw new UnsupportedOperationException();
    }

}
