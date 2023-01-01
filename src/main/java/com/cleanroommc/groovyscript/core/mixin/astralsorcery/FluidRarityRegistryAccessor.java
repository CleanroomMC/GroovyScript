package com.cleanroommc.groovyscript.core.mixin.astralsorcery;

import hellfirepvp.astralsorcery.common.base.FluidRarityRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin( value = FluidRarityRegistry.class, remap = false )
public interface FluidRarityRegistryAccessor {

    @Accessor
    public List<FluidRarityRegistry.FluidRarityEntry> getRarityList();

}
