/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.core.mixin.woot;

import ipsis.woot.loot.customdrops.CustomDropsRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = CustomDropsRepository.class, remap = false)
public interface CustomDropsRepositoryAccessor {

    @Accessor
    List<Object> getDrops();
}
