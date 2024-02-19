package com.cleanroommc.groovyscript.core.mixin.tconstruct;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.traits.ITrait;

import java.util.List;
import java.util.Map;

@Mixin(value = Material.class, remap = false)
public interface MaterialAccessor {

    @Accessor
    Map<String, List<ITrait>> getTraits();
}
