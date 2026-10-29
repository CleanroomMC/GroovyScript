/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.core.mixin.astralsorcery;

import hellfirepvp.astralsorcery.common.base.FluidRarityRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = FluidRarityRegistry.class, remap = false)
public interface FluidRarityRegistryAccessor {

    @Accessor
    List<FluidRarityRegistry.FluidRarityEntry> getRarityList();
}
