/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.core.mixin.ic2;

import ic2.api.recipe.IElectrolyzerRecipeManager;
import ic2.core.recipe.ElectrolyzerRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = ElectrolyzerRecipeManager.class, remap = false)
public interface ElectrolyzerRecipeManagerAccessor {

    @Accessor
    Map<String, IElectrolyzerRecipeManager.ElectrolyzerRecipe> getFluidMap();
}
