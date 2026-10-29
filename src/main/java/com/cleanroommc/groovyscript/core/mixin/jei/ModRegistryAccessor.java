/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.core.mixin.jei;

import mezz.jei.collect.ListMultiMap;
import mezz.jei.startup.ModRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ModRegistry.class, remap = false)
public interface ModRegistryAccessor {

    @Accessor
    ListMultiMap<String, Object> getRecipeCatalysts();
}
