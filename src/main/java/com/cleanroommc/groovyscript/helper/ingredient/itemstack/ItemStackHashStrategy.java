package com.cleanroommc.groovyscript.helper.ingredient.itemstack;

import it.unimi.dsi.fastutil.Hash;
import net.minecraft.item.ItemStack;

public class ItemStackHashStrategy implements Hash.Strategy<ItemStack> {

    public static final ItemStackHashStrategy STRATEGY = new ItemStackHashStrategy();

    @Override
    public int hashCode(ItemStack o) {
        return 31 * o.getItem().hashCode() + o.getMetadata();
    }

    @Override
    public boolean equals(ItemStack a, ItemStack b) {
        return a == b || (a != null && b != null && ItemStack.areItemsEqual(a, b));
    }
}

