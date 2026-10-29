/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.core.mixin.forestry;

import forestry.api.recipes.IFermenterRecipe;
import forestry.factory.recipes.FermenterRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(value = FermenterRecipeManager.class, remap = false)
public interface FermenterRecipeManagerAccessor {

    @Accessor
    static Set<IFermenterRecipe> getRecipes() {
        throw new AssertionError();
    }
}
