package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.thermalexpansion.util.managers.machine.CompactorManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = CompactorManager.class, remap = false)
public interface CompactorManagerAccessor {

    @Accessor
    static Map<ComparableItemStackValidatedNBT, CompactorManager.CompactorRecipe> getRecipeMapAll() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Map<ComparableItemStackValidatedNBT, CompactorManager.CompactorRecipe> getRecipeMapPlate() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Map<ComparableItemStackValidatedNBT, CompactorManager.CompactorRecipe> getRecipeMapCoin() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Map<ComparableItemStackValidatedNBT, CompactorManager.CompactorRecipe> getRecipeMapGear() {
        throw new UnsupportedOperationException();
    }

}
