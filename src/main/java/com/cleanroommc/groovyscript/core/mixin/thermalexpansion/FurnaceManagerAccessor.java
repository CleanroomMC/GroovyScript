package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidated;
import cofh.thermalexpansion.util.managers.machine.FurnaceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.Set;

@Mixin(value = FurnaceManager.class, remap = false)
public interface FurnaceManagerAccessor {

    @Accessor
    static Map<ComparableItemStackValidated, FurnaceManager.FurnaceRecipe> getRecipeMap() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Map<ComparableItemStackValidated, FurnaceManager.FurnaceRecipe> getRecipeMapPyrolysis() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Set<ComparableItemStackValidated> getFoodSet() {
        throw new UnsupportedOperationException();
    }

}
