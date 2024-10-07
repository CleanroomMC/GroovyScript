package com.cleanroommc.groovyscript.core.mixin.astralsorcery;

import hellfirepvp.astralsorcery.common.base.OreTypes;
import hellfirepvp.astralsorcery.common.base.sets.OreEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(value = OreTypes.class, remap = false)
public interface OreTypesAccessor {

    @Accessor("oreDictWeights")
    List<OreEntry> getEntries();

    @Accessor
    double getTotalWeight();

    @Accessor
    void setTotalWeight(double weight);

    @Invoker("appendOreEntry")
    void add(OreEntry entry);
}
