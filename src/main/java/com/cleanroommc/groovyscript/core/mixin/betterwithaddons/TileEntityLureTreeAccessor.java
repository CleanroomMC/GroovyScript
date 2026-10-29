/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.core.mixin.betterwithaddons;

import betterwithaddons.tileentity.TileEntityLureTree;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.ArrayList;

@Mixin(value = TileEntityLureTree.class, remap = false)
public interface TileEntityLureTreeAccessor {

    @Accessor("BLACKLIST")
    static ArrayList<Class<? extends Entity>> getBlacklist() {
        throw new UnsupportedOperationException();
    }
}
