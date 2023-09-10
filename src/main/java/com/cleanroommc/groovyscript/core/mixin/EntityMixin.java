package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.api.IEntityAccess;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public class EntityMixin implements IEntityAccess {

    @Shadow
    public int fire;

    @Override
    public int getFire() {
        return fire;
    }
}
