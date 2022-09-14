package com.cleanroommc.groovyscript.compat.vanilla;

import net.minecraft.item.ItemStack;

import java.util.Map;

public interface FurnaceRecipeAccess {

    Map<ItemStack, ItemStack> getSmeltingList();

    Map<ItemStack, Float> getExperienceList();

    Map<ItemStack, ItemStack> getInputList();

    boolean invokeCompareItemStacks(ItemStack stack1, ItemStack stack2);
}
