package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.thermalexpansion.util.managers.machine.SmelterManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(value = SmelterManager.class, remap = false)
public interface SmelterManagerAccessor {

    @Accessor
    static Map<List<ComparableItemStackValidatedNBT>, SmelterManager.SmelterRecipe> getRecipeMap() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Set<ComparableItemStackValidatedNBT> getValidationSet() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Set<ComparableItemStackValidatedNBT> getLockSet() {
        throw new UnsupportedOperationException();
    }

}
