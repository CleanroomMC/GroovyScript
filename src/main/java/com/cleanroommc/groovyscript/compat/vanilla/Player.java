package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Player implements IScriptReloadable {

    public static final String GIVEN_ITEMS = "GroovyScript:GivenItems";

    public boolean testingStartingItems = false;

    private final List<ItemStack> givenItemsAnySlot = new ArrayList<>();

    private ItemStack[] givenItemsSlots = new ItemStack[36];

    @GroovyBlacklist
    public void addToInventory(InventoryPlayer playerInv) {
        for (int i = 0; i < givenItemsSlots.length; i++) {
            ItemStack stack = givenItemsSlots[i];
            if (stack != null && !stack.isEmpty()) {
                playerInv.add(i, stack.copy());
            }
        }
        givenItemsAnySlot.stream().map(ItemStack::copy).forEach(playerInv::addItemStackToInventory);
    }

    public void addStartingItem(ItemStack item) {
        this.addStartingItem(item, -1);
    }

    public void addStartingItem(ItemStack item, int slot) {
        if (slot >= 36) {
            GroovyLog.msg("Warning: assigning items to a player's inventory slot greater than 36 may cause some items to not be received by the player.")
                    .warn().post();
        }
        if (slot <= -1) {
            this.givenItemsAnySlot.add(item);
        } else {
            if (slot >= givenItemsSlots.length) {
                ItemStack[] oldGivenItemsSlots = givenItemsSlots;
                givenItemsSlots = Arrays.copyOf(oldGivenItemsSlots, slot + 1);
            }
            if (givenItemsSlots[slot] != null) {
                GroovyLog.msg("Error: slot {} has already been occupied by another item.", slot)
                        .error().post();
            }
        }
    }

    @Override
    public void onReload() {
        this.givenItemsAnySlot.clear();
        this.givenItemsSlots = new ItemStack[36];
    }

    @Override
    public void afterScriptLoad() {

    }
}
