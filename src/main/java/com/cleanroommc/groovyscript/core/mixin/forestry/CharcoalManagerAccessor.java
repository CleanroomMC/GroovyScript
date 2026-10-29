/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.core.mixin.forestry;

import forestry.api.arboriculture.ICharcoalPileWall;
import forestry.arboriculture.charcoal.CharcoalManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = CharcoalManager.class, remap = false)
public interface CharcoalManagerAccessor {

    @Accessor
    List<ICharcoalPileWall> getWalls();
}
