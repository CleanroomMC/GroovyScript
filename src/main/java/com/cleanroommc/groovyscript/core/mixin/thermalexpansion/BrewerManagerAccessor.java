package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.thermalexpansion.util.managers.machine.BrewerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(value = BrewerManager.class, remap = false)
public interface BrewerManagerAccessor {

    @Accessor
    static Map<List<Integer>, BrewerManager.BrewerRecipe> getRecipeMap() {
        throw new AssertionError();
    }

    @Accessor
    static Set<ComparableItemStackValidatedNBT> getValidationSet() {
        throw new AssertionError();
    }

    @Accessor
    static Set<String> getValidationFluids() {
        throw new AssertionError();
    }

}
