/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.core.mixin.thebetweenlands;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import thebetweenlands.api.recipes.ICrabPotFilterRecipeBubbler;
import thebetweenlands.common.recipe.misc.CrabPotFilterRecipeBubbler;

import java.util.List;

@Mixin(value = CrabPotFilterRecipeBubbler.class, remap = false)
public interface CrabPotFilterRecipeBubblerAccessor {

    @Accessor("RECIPES")
    static List<ICrabPotFilterRecipeBubbler> getRecipes() {
        throw new UnsupportedOperationException();
    }
}
