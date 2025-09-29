package com.cleanroommc.groovyscript.helper.ingredient.itemstack;

import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Set;

/**
 * Some Minecraft logic functions different when interacting with
 * {@link ItemStack}s with metadata equal to {@link Short#MAX_VALUE}.
 * This class handles this logic via two sets -
 * one for if the {@link ItemStack} being checked has wildcard metadata,
 * and the other for if it doesn't.
 */
public class ItemStackSet {

    private final Set<Item> wildcard = new ObjectOpenHashSet<>();
    private final Set<ItemStack> metadata = new ObjectOpenCustomHashSet<>(ItemStackHashStrategy.STRATEGY);

    public boolean add(ItemStack k) {
        if (k.getItemDamage() == Short.MAX_VALUE) return wildcard.add(k.getItem());
        return metadata.add(k);
    }

    public boolean remove(ItemStack k) {
        if (k.getItemDamage() == Short.MAX_VALUE) return wildcard.remove(k.getItem());
        return metadata.remove(k);
    }

    public boolean contains(ItemStack k) {
        if (k.getItemDamage() == Short.MAX_VALUE) return wildcard.contains(k.getItem());
        return metadata.contains(k);
    }

    public boolean containsAsWildcard(ItemStack k) {
        return wildcard.contains(k.getItem());
    }

    public void clear() {
        wildcard.clear();
        metadata.clear();
    }
}
