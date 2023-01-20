package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyLog;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class Player {

    public static final String GIVEN_ITEMS = "GroovyScript:GivenItems";
    public static final Map<ItemStack, Integer> ITEM_MAP = new HashMap<>();

    public void addStartingItem(ItemStack item) {
        this.addStartingItem(item, -1);
    }

    public void addStartingItem(ItemStack item, int slot) {
        if (ITEM_MAP.size() > 36) GroovyLog.msg("Warning: adding more than 36 items to a player's inventory may cause some items to not be received by the player.").warn().post();
        if (slot > 36) GroovyLog.msg("Warning: assigning items to a player's inventory slot greater than 36 may cause some items to not be received by the player.").warn().post();
        ITEM_MAP.put(item, slot);
    }

}
