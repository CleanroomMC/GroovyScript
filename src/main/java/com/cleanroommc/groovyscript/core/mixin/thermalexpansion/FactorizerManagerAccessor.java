package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidated;
import cofh.thermalexpansion.util.managers.device.FactorizerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = FactorizerManager.class, remap = false)
public interface FactorizerManagerAccessor {

    @Accessor
    static Map<ComparableItemStackValidated, FactorizerManager.FactorizerRecipe> getRecipeMap() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Map<ComparableItemStackValidated, FactorizerManager.FactorizerRecipe> getRecipeMapReverse() {
        throw new UnsupportedOperationException();
    }

}
