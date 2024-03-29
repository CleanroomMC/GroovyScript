package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.core.util.ItemWrapper;
import cofh.thermalexpansion.util.managers.machine.ExtruderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = ExtruderManager.class, remap = false)
public interface ExtruderManagerAccessor {

    @Accessor
    static Map<ItemWrapper, ExtruderManager.ExtruderRecipe> getRecipeMapIgneous() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Map<ItemWrapper, ExtruderManager.ExtruderRecipe> getRecipeMapSedimentary() {
        throw new UnsupportedOperationException();
    }

}
