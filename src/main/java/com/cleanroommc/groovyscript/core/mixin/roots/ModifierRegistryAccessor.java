package com.cleanroommc.groovyscript.core.mixin.roots;

import epicsquid.roots.modifiers.ModifierRegistry;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(ModifierRegistry.class)
public interface ModifierRegistryAccessor {

    @Accessor
    static Set<ResourceLocation> getDisabledModifiers() {
        throw new UnsupportedOperationException();
    }
}
