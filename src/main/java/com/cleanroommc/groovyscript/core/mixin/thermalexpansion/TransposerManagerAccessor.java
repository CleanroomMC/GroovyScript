package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.core.util.ItemWrapper;
import cofh.thermalexpansion.util.managers.machine.TransposerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(value = TransposerManager.class, remap = false)
public interface TransposerManagerAccessor {

    @Accessor
    static Map<List<Integer>, TransposerManager.TransposerRecipe> getRecipeMapFill() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Map<ComparableItemStackValidatedNBT, TransposerManager.TransposerRecipe> getRecipeMapExtract() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Map<ItemWrapper, TransposerManager.ContainerOverride> getContainerOverrides() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Set<ComparableItemStackValidatedNBT> getValidationSet() {
        throw new UnsupportedOperationException();
    }

}
