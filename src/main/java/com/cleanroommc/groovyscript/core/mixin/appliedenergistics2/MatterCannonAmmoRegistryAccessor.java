/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.core.mixin.appliedenergistics2;

import appeng.core.features.registries.MatterCannonAmmoRegistry;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;

@Mixin(value = MatterCannonAmmoRegistry.class, remap = false)
public interface MatterCannonAmmoRegistryAccessor {

    @Accessor("DamageModifiers")
    HashMap<ItemStack, Double> getDamageModifiers();
}
