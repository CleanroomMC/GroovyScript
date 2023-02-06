package com.cleanroommc.groovyscript.core.mixin.astralsorcery;

import hellfirepvp.astralsorcery.common.base.LiquidInteraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin( value = LiquidInteraction.class, remap = false )
public interface LiquidInteractionAccessor {

    @Accessor
    static List<LiquidInteraction> getRegisteredInteractions() {
        return null;
    }

    @Accessor("action")
    LiquidInteraction.FluidInteractionAction getFluidInteractionAction();

}
