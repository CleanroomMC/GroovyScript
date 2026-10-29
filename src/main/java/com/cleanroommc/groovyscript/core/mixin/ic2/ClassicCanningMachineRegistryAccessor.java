/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.core.mixin.ic2;

import ic2.core.block.machine.recipes.managers.CanningMachineRegistry;
import ic2.core.util.helpers.ItemWithMeta;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(value = CanningMachineRegistry.class, remap = false)
public interface ClassicCanningMachineRegistryAccessor {

    @Accessor("IdToItems")
    Map<Integer, List<ItemWithMeta>> getIdToItems();
}
