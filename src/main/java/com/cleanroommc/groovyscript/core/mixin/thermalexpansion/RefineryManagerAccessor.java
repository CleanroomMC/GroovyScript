package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.RefineryManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(value = RefineryManager.class, remap = false)
public interface RefineryManagerAccessor {

    @Accessor
    static Int2ObjectOpenHashMap<RefineryManager.RefineryRecipe> getRecipeMap() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Int2ObjectOpenHashMap<RefineryManager.RefineryRecipe> getRecipeMapPotion() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Set<String> getBioFluids() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Set<String> getFossilFluids() {
        throw new UnsupportedOperationException();
    }

}
