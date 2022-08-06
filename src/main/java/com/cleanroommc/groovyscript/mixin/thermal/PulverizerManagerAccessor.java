package com.cleanroommc.groovyscript.mixin.thermal;

import cofh.core.inventory.ComparableItemStackValidated;
import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = PulverizerManager.class, remap = false)
public interface PulverizerManagerAccessor {

    @Accessor
    static Map<ComparableItemStackValidated, PulverizerManager.PulverizerRecipe> getRecipeMap() {
        return null;
    }
}
