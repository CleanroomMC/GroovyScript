package com.cleanroommc.groovyscript.core.mixin.jei;

import mezz.jei.collect.ListMultiMap;
import mezz.jei.startup.ModRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ModRegistry.class, remap = false)
public interface ModRegistryAccessor {

    @Accessor
    ListMultiMap<String, Object> getRecipeCatalysts();

}
