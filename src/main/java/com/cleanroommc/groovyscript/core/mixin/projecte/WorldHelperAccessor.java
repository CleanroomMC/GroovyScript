package com.cleanroommc.groovyscript.core.mixin.projecte;

import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.EntityLiving;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = WorldHelper.class, remap = false)
public interface WorldHelperAccessor {

    @Accessor
    static List<Class<? extends EntityLiving>> getPeacefuls() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static List<Class<? extends EntityLiving>> getMobs() {
        throw new UnsupportedOperationException();
    }

}
