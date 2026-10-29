/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.compat.vanilla;

import groovy.lang.Closure;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ICraftingRecipe {

    @Nullable
    Closure<Void> getRecipeAction();

    @Nullable
    Closure<ItemStack> getRecipeFunction();
}
