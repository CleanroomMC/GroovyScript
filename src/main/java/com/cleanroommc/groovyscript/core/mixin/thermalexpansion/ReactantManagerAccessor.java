package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.core.inventory.ComparableItemStack;
import cofh.thermalexpansion.util.managers.dynamo.ReactantManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(value = ReactantManager.class, remap = false)
public interface ReactantManagerAccessor {

    @Accessor
    static Map<List<Integer>, ReactantManager.Reaction> getReactionMap() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Set<ComparableItemStack> getValidReactants() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Set<String> getValidFluids() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Set<ComparableItemStack> getValidReactantsElemental() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Set<String> getValidFluidsElemental() {
        throw new UnsupportedOperationException();
    }

}
