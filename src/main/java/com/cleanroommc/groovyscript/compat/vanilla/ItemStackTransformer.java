/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.compat.vanilla;

import net.minecraft.item.ItemStack;

public interface ItemStackTransformer {

    ItemStack transform(ItemStack self);
}
