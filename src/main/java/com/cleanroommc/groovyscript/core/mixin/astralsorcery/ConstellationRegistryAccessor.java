package com.cleanroommc.groovyscript.core.mixin.astralsorcery;

import hellfirepvp.astralsorcery.common.constellation.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin( value = ConstellationRegistry.class, remap = false )
public interface ConstellationRegistryAccessor {

    @Accessor("generalConstellationList")
    public static List<IConstellation> getConstellationList() {
        return null;
    }

    @Accessor
    public static List<IMajorConstellation> getMajorConstellations() {
        return null;
    }

    @Accessor
    public static List<IWeakConstellation> getWeakConstellations() {
        return null;
    }

    @Accessor
    public static List<IMinorConstellation> getMinorConstellations() {
        return null;
    }

}
