package com.cleanroommc.groovyscript.core.mixin.astralsorcery;

import hellfirepvp.astralsorcery.common.data.research.ResearchNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ResearchNode.class, remap = false)
public interface ResearchNodeAccessor {

    @Accessor("renderPosX")
    void setX(int x);

    @Accessor("renderPosZ")
    void setZ(int z);
}
