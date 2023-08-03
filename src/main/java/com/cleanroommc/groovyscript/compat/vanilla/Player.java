package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class Player implements IScriptReloadable {

    public static final String GIVEN_ITEMS = "GroovyScript:GivenItems";

    public boolean testingStartingItems = false;
    public boolean replaceDefaultInventory = false;

    private final List<ItemStack> givenItemsAnySlot = new ArrayList<>();

    private final Map<Integer, ItemStack> givenItemsSlots = new Int2ObjectOpenHashMap<>();

    @GroovyBlacklist
    public void addToInventory(InventoryPlayer playerInv) {
        if (replaceDefaultInventory) playerInv.clear();

        for (Map.Entry<Integer, ItemStack> entry : givenItemsSlots.entrySet()) {
            if (replaceDefaultInventory) {
                playerInv.setInventorySlotContents(entry.getKey(), entry.getValue().copy());
            } else {
                if (entry.getValue().isEmpty()) continue;

                if (playerInv.getStackInSlot(entry.getKey()).isEmpty() || playerInv.getStackInSlot(entry.getKey()).equals(entry.getValue())) {
                    playerInv.add(entry.getKey(), entry.getValue().copy());
                } else {
                    GroovyLog.msg("Could not set inventory slot {} to itemstack {}", entry.getKey(), entry.getValue()).error().post();
                }
            }
        }
        givenItemsAnySlot.stream().map(ItemStack::copy).forEach(playerInv::addItemStackToInventory);
    }

    public void addStartingItem(ItemStack item) {
        this.addStartingItem(item, -1);
    }

    public void addStartingItem(ItemStack item, int slot) {
        if (slot > 41) {
            GroovyLog.msg("Warning: assigning items to a player's inventory slot greater than 41 may cause some items to not be received by the player.")
                    .warn().post();
        }
        if (slot <= -1) {
            givenItemsAnySlot.add(item == null ? ItemStack.EMPTY : item);
        } else {
            if (givenItemsSlots.get(slot) != null) {
                GroovyLog.msg("Warning: slot {} has already been occupied by another item.", slot)
                        .error().post();
                return;
            }
            givenItemsSlots.put(slot, item == null ? ItemStack.EMPTY : item);
        }
    }

    public void setStartingItems(boolean isSlotSpecific, ItemStack... items) {
        if (items.length > 41) {
            GroovyLog.msg("Warning: assigning items to a player's inventory slot greater than 41 may cause some items to not be received by the player.")
                    .warn().post();
        }
        if (isSlotSpecific) {
            givenItemsSlots.clear();
            for (int i = 0; i < items.length; i++) {
                givenItemsSlots.put(i, items[i] == null ? ItemStack.EMPTY : items[i]);
            }
        } else {
            givenItemsAnySlot.clear();
            givenItemsAnySlot.addAll(Arrays.stream(items).filter(Objects::nonNull).collect(Collectors.toList()));
        }
    }

    public void setStartingItems(boolean isSlotSpecific, List<ItemStack> items) {
        if (items.size() > 41) {
            GroovyLog.msg("Warning: assigning items to a player's inventory slot greater than 41 may cause some items to not be received by the player.")
                    .warn().post();
        }
        if (isSlotSpecific) {
            givenItemsSlots.clear();
            for (int i = 0; i < items.size(); i++) {
                givenItemsSlots.put(i, items.get(0) == null ? ItemStack.EMPTY : items.get(0));
            }
        } else {
            givenItemsAnySlot.clear();
            givenItemsAnySlot.addAll(items.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        }
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        givenItemsAnySlot.clear();
        givenItemsSlots.clear();
    }

    @Override
    @GroovyBlacklist
    public void afterScriptLoad() {

    }
}
