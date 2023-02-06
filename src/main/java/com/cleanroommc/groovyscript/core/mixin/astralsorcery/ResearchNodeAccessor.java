package com.cleanroommc.groovyscript.core.mixin.astralsorcery;

import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin( value = ResearchNode.class, remap = false )
public interface ResearchNodeAccessor {

    @Accessor("renderPosX")
    public void setX(int x);

    @Accessor("renderPosZ")
    public void setZ(int z);

}
