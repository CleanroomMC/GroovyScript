package com.cleanroommc.groovyscript.core.mixin.betterwithmods;

import betterwithmods.common.registry.heat.BWMHeatRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = BWMHeatRegistry.class, remap = false)
public interface BWMHeatRegistryAccessor {

    @Accessor
    static List<BWMHeatRegistry.HeatSource> getHEAT_SOURCES() {
        throw new UnsupportedOperationException();
    }

}
