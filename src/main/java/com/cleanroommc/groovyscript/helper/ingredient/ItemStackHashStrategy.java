package com.cleanroommc.groovyscript.helper.ingredient;

import it.unimi.dsi.fastutil.Hash;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Objects;

public class ItemStackHashStrategy implements Hash.Strategy<ItemStack> {

    public static final ItemStackHashStrategy STRATEGY = new ItemStackHashStrategy();

    @Override
    public int hashCode(ItemStack o) {
        return Objects.hash(o.getItem(), o.getMetadata());
    }

    @Override
    public boolean equals(ItemStack a, ItemStack b) {
        return a == b || (a != null && b != null && OreDictionary.itemMatches(a, b, false));
    }
}
