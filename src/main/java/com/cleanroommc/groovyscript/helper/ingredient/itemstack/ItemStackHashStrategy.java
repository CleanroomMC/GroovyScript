package com.cleanroommc.groovyscript.helper.ingredient.itemstack;

import it.unimi.dsi.fastutil.Hash;
import net.minecraft.item.ItemStack;

/**
 * Hash strategy for fastutils that checks item and metadata.
 * Note that in many cases, metadata equal to {@link Short#MAX_VALUE}
 * (aka {@link net.minecraftforge.oredict.OreDictionary#WILDCARD_VALUE OreDictionary.WILDCARD_VALUE})
 * has special logic, and will need to be handled separately from the other ItemStacks.
 * <br>
 * This cannot be part of the hash strategy, as doing so would require
 * violating {@link Object#hashCode()}.
 */
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
