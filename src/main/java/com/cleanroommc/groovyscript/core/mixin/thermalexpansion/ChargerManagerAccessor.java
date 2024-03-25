package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.thermalexpansion.util.managers.machine.ChargerManager;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.Set;

@Mixin(value = ChargerManager.class, remap = false)
public interface ChargerManagerAccessor {

    @Accessor
    static Map<ComparableItemStackValidatedNBT, ChargerManager.ChargerRecipe> getRecipeMap() {
        throw new AssertionError();
    }

}
