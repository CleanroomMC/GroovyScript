/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.thermalexpansion.util.managers.machine.CrucibleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.Set;

@Mixin(value = CrucibleManager.class, remap = false)
public interface CrucibleManagerAccessor {

    @Accessor
    static Map<ComparableItemStackValidatedNBT, CrucibleManager.CrucibleRecipe> getRecipeMap() {
        throw new AssertionError();
    }

    @Accessor
    static Set<ComparableItemStackValidatedNBT> getLavaSet() {
        throw new AssertionError();
    }
}
