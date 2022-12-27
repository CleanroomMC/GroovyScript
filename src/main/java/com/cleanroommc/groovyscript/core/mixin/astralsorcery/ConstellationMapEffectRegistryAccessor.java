package com.cleanroommc.groovyscript.core.mixin.astralsorcery;

import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.starmap.ConstellationMapEffectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin( value = ConstellationMapEffectRegistry.class, remap = false )
public interface ConstellationMapEffectRegistryAccessor {

    @Accessor
    public static Map<IConstellation, ConstellationMapEffectRegistry.MapEffect> getEffectRegistry() {
        return null;
    }

}
