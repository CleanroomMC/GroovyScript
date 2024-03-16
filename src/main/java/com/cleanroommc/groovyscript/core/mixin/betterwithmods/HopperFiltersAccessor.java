package com.cleanroommc.groovyscript.core.mixin.betterwithmods;

import betterwithmods.api.tile.IHopperFilter;
import betterwithmods.common.registry.HopperFilters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = HopperFilters.class, remap = false)
public interface HopperFiltersAccessor {

    @Accessor
    Map<String, IHopperFilter> getFILTERS();

}
