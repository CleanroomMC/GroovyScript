package com.cleanroommc.groovyscript.core.mixin.astralsorcery;

import hellfirepvp.astralsorcery.common.constellation.ConstellationBase;
import hellfirepvp.astralsorcery.common.crafting.ItemHandle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin( value = ConstellationBase.class, remap = false )
public interface ConstellationBaseAccessor {

    @Accessor
    public List<ItemHandle> getSignatureItems();

}
