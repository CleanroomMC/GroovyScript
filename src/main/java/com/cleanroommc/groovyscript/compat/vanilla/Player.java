package com.cleanroommc.groovyscript.compat.vanilla;

import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class Player {

    public static final String givenItems = "groovyscript_given_items";
    public static final Map<ItemStack, Integer> itemMap = new HashMap<>();

    public void addStartingItem(ItemStack item) {
        itemMap.put(item, -1);
    }

    public void addStartingItem(ItemStack item, int slot) {
        itemMap.put(item, slot);
    }

}
