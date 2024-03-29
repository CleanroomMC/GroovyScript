package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.core.util.ItemWrapper;
import cofh.thermalexpansion.util.managers.machine.PrecipitatorManager;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;

@Mixin(value = PrecipitatorManager.class, remap = false)
public interface PrecipitatorManagerAccessor {

    @org.spongepowered.asm.mixin.gen.Accessor
    static Map<ItemWrapper, PrecipitatorManager.PrecipitatorRecipe> getRecipeMap() {
        throw new UnsupportedOperationException();
    }

}
