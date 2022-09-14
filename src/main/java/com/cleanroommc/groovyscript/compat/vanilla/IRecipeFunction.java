package com.cleanroommc.groovyscript.compat.vanilla;

import net.minecraft.item.ItemStack;

import java.util.Map;

public interface IRecipeFunction {
    ItemStack apply(ItemStack output, Map<String, ItemStack> markedInputs, CraftingInfo info);
}
