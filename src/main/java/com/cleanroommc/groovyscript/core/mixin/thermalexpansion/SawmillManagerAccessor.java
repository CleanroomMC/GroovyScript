package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.thermalexpansion.util.managers.machine.SawmillManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = SawmillManager.class, remap = false)
public interface SawmillManagerAccessor {

    @Accessor
    static Map<ComparableItemStackValidatedNBT, SawmillManager.SawmillRecipe> getRecipeMap() {
        throw new UnsupportedOperationException();
    }

}
