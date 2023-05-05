package com.cleanroommc.groovyscript.core.mixin.roots;

import epicsquid.roots.ritual.RitualBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RitualBase.class)
public interface RitualBaseAccessor {

    @Accessor("disabled")
    boolean getDisabled();

    @Accessor("disabled")
    void setDisabled(boolean value);
}
