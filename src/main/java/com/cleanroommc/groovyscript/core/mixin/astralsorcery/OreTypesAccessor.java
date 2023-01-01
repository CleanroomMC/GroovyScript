package com.cleanroommc.groovyscript.core.mixin.astralsorcery;

import hellfirepvp.astralsorcery.common.base.OreTypes;
import hellfirepvp.astralsorcery.common.base.sets.OreEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin( value = OreTypes.class , remap = false )
public interface OreTypesAccessor {

    @Accessor("oreDictWeights")
    public List<OreEntry> getEntries();

    @Accessor
    public double getTotalWeight();

    @Accessor
    public void setTotalWeight(double weight);

    @Invoker("appendOreEntry")
    public void add(OreEntry entry);

}
