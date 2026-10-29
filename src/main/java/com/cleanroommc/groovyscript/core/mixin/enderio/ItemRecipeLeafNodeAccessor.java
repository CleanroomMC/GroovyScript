/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.core.mixin.enderio;

import com.enderio.core.common.util.NNList;
import crazypants.enderio.base.recipe.lookup.IRecipeNode;
import crazypants.enderio.base.recipe.lookup.ItemRecipeLeafNode;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ItemRecipeLeafNode.class, remap = false)
public interface ItemRecipeLeafNodeAccessor<REC> extends IRecipeNode<REC, Item, Integer> {

    @Accessor
    Int2ObjectOpenHashMap<NNList<REC>> getMap();
}
