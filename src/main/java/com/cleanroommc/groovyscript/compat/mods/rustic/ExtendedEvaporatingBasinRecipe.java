/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.compat.mods.rustic;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import rustic.common.crafting.EvaporatingBasinRecipe;

public class ExtendedEvaporatingBasinRecipe extends EvaporatingBasinRecipe {

    private final int time;

    public ExtendedEvaporatingBasinRecipe(ItemStack out, FluidStack in, int time) {
        super(out, in);
        this.time = time;
    }

    @Override
    public int getTime() {
        return time;
    }
}
