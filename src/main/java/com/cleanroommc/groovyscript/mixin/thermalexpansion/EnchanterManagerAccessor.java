package com.cleanroommc.groovyscript.mixin.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.thermalexpansion.util.managers.machine.EnchanterManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(EnchanterManager.class)
public interface EnchanterManagerAccessor {

    @Accessor
    static Map<List<ComparableItemStackValidatedNBT>, EnchanterManager.EnchanterRecipe> getRecipeMap() {
        throw new AssertionError();
    }
}
