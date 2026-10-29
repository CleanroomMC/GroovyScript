/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.core.inventory.ComparableItemStack;
import cofh.thermalexpansion.util.managers.device.XpCollectorManager;
import gnu.trove.map.hash.TObjectIntHashMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = XpCollectorManager.class, remap = false)
public interface XpCollectorManagerAccessor {

    @Accessor
    static TObjectIntHashMap<ComparableItemStack> getCatalystMap() {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static TObjectIntHashMap<ComparableItemStack> getCatalystFactorMap() {
        throw new UnsupportedOperationException();
    }
}
