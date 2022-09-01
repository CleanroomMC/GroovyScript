package com.cleanroommc.groovyscript.mixin.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.thermalexpansion.util.managers.machine.CompactorManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(CompactorManager.class)
public interface CompactorManagerAccessor {

    @Accessor
    static Map<ComparableItemStackValidatedNBT, CompactorManager.CompactorRecipe> getRecipeMapAll() {
        throw new AssertionError();
    }

    @Accessor
    static Map<ComparableItemStackValidatedNBT, CompactorManager.CompactorRecipe> getRecipeMapPlate() {
        throw new AssertionError();
    }

    @Accessor
    static Map<ComparableItemStackValidatedNBT, CompactorManager.CompactorRecipe> getRecipeMapCoin() {
        throw new AssertionError();
    }

    @Accessor
    static Map<ComparableItemStackValidatedNBT, CompactorManager.CompactorRecipe> getRecipeMapGear() {
        throw new AssertionError();
    }
}
