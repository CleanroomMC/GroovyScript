package com.cleanroommc.groovyscript.helper.ingredient.itemstack;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;


/**
 * Some Minecraft logic functions different when interacting with
 * {@link ItemStack}s with metadata equal to {@link Short#MAX_VALUE} ({@link net.minecraftforge.oredict.OreDictionary#WILDCARD_VALUE}.
 * <p>
 * This class handles this logic via two maps -
 * {@link Object2IntOpenHashMap} and {@link Object2IntOpenCustomHashMap} (with the hash strategy being {@link ItemStackHashStrategy#STRATEGY}.
 * The former is for if the {@link ItemStack} being checked has wildcard metadata,
 * and the latter is for if it doesn't.
 * <p>
 * This means that insertion inserts into one of two maps depending on metadata,
 * and retrieval first checks the wildcard map before checking the metadata-specific map.
 */
public class ItemStack2IntProxyMap {

    private final Object2IntMap<Item> wildcard = new Object2IntOpenHashMap<>();
    private final Object2IntMap<ItemStack> metadata = new Object2IntOpenCustomHashMap<>(ItemStackHashStrategy.STRATEGY);

    public int put(ItemStack key, int value) {
        if (key.getItemDamage() == OreDictionary.WILDCARD_VALUE) return wildcard.put(key.getItem(), value);
        return metadata.put(key, value);
    }

    public int removeInt(ItemStack key) {
        if (key.getItemDamage() == OreDictionary.WILDCARD_VALUE) return wildcard.removeInt(key.getItem());
        return metadata.removeInt(key);
    }

    public int getInt(ItemStack key) {
        if (wildcard.containsKey(key.getItem())) wildcard.getInt(key.getItem());
        return metadata.getInt(key);
    }

    public void clear() {
        wildcard.clear();
        metadata.clear();
    }
}
