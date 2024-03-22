package com.cleanroommc.groovyscript.core.mixin.extrautils2;

import com.rwtema.extrautils2.api.machine.MachineSlotItem;
import com.rwtema.extrautils2.machine.MachineInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = MachineInit.class, remap = false)
public interface MachineInitAccessor {

    @Accessor
    static MachineSlotItem getSLOT_SLIME_SECONDARY() {
        throw new UnsupportedOperationException();
    }
}
