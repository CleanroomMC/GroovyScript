package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(value = InsolatorManager.class, remap = false)
public interface InsolatorManagerAccessor {

    @Accessor
    static Map<List<ComparableItemStackValidatedNBT>, InsolatorManager.InsolatorRecipe> getRecipeMap() {
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
