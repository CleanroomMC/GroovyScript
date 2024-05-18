package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidated;
import cofh.thermalexpansion.util.managers.machine.ChargerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = ChargerManager.class, remap = false)
public interface ChargerManagerAccessor {

    @Accessor
    static Map<ComparableItemStackValidated, ChargerManager.ChargerRecipe> getRecipeMap() {
        throw new UnsupportedOperationException();
    }

}
