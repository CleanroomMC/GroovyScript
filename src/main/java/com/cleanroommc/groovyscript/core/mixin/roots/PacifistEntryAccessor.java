/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.core.mixin.roots;

import epicsquid.roots.recipe.PacifistEntry;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = PacifistEntry.class, remap = false)
public interface PacifistEntryAccessor {

    @Accessor
    void setName(ResourceLocation name);
}
