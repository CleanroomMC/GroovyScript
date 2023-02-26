package com.cleanroommc.groovyscript.core.mixin.astralsorcery;

import hellfirepvp.astralsorcery.common.constellation.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = ConstellationRegistry.class, remap = false)
public interface ConstellationRegistryAccessor {

    @Accessor("generalConstellationList")
    static List<IConstellation> getConstellationList() {
        return null;
    }

    @Accessor
    static List<IMajorConstellation> getMajorConstellations() {
        return null;
    }

    @Accessor
    static List<IWeakConstellation> getWeakConstellations() {
        return null;
    }

    @Accessor
    static List<IMinorConstellation> getMinorConstellations() {
        return null;
    }

}
