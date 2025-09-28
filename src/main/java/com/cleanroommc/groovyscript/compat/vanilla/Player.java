package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.registry.NamedRegistry;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES, admonition = {
        @Admonition(type = Admonition.Type.WARNING, value = "groovyscript.wiki.minecraft.player.note0"),
        @Admonition(type = Admonition.Type.TIP, value = "groovyscript.wiki.minecraft.player.note1")
})
public class Player extends NamedRegistry implements IScriptReloadable {

    public static final String GIVEN_ITEMS = "GroovyScript:GivenItems";
    private final List<ItemStack> givenItemsAnySlot = new ArrayList<>();
    private final Map<Integer, ItemStack> givenItemsSlots = new Int2ObjectOpenHashMap<>();
    private boolean testingStartingItems;
    private boolean replaceDefaultInventory;

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

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.player.addStartingItem0", example = {
            @Example("item('minecraft:clay_ball')"),
            @Example("item('minecraft:gold_ingot')"),
            @Example("item('minecraft:diamond')"),
            @Example("item('minecraft:nether_star')"),
            @Example("item('minecraft:water_bucket')"),
    })
    public void addStartingItem(ItemStack item) {
        this.addStartingItem(item, -1);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, description = "groovyscript.wiki.minecraft.player.addStartingItem1")
    public void addStartingItem(ItemStack item, int slot) {
        if (slot > 41) {
            GroovyLog.msg("Warning: assigning items to a player's inventory slot greater than 41 may cause some items to not be received by the player.")
                    .warn()
                    .post();
        }
        if (slot <= -1) {
            givenItemsAnySlot.add(item == null ? ItemStack.EMPTY : item);
        } else {
            if (givenItemsSlots.get(slot) != null) {
                GroovyLog.msg("Warning: slot {} has already been occupied by another item.", slot)
                        .error()
                        .post();
                return;
            }
            givenItemsSlots.put(slot, item == null ? ItemStack.EMPTY : item);
        }
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("true, item('minecraft:clay').withNbt([display:[Name:'Hotbar']]), null, null, null, null, null, null, null, null, item('minecraft:clay').withNbt([display:[Name:'Top row of inventory']]), null, null, null, null, null, null, null, null, item('minecraft:clay').withNbt([display:[Name:'Middle row of inventory']]), null, null, null, null, null, null, null, null, item('minecraft:clay').withNbt([display:[Name:'Bottom row of inventory']]), null, null, null, null, null, null, null, null, item('minecraft:diamond_boots'), item('minecraft:diamond_leggings'), item('minecraft:diamond_chestplate'), item('minecraft:diamond_helmet'), item('minecraft:clay').withNbt([display:[Name:'Offhand']])"))
    public void setStartingItems(boolean isSlotSpecific, ItemStack... items) {
        if (items.length > 41) {
            GroovyLog.msg("Warning: assigning items to a player's inventory slot greater than 41 may cause some items to not be received by the player.")
                    .warn()
                    .post();
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

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void setStartingItems(boolean isSlotSpecific, List<ItemStack> items) {
        if (items.size() > 41) {
            GroovyLog.msg("Warning: assigning items to a player's inventory slot greater than 41 may cause some items to not be received by the player.")
                    .warn()
                    .post();
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

    @MethodDescription(type = MethodDescription.Type.VALUE, example = @Example(value = "true", commented = true))
    public void setTestStartingItems(boolean value) {
        testingStartingItems = value;
    }

    @MethodDescription(type = MethodDescription.Type.VALUE, example = @Example("true"))
    public void setReplaceDefaultInventory(boolean value) {
        replaceDefaultInventory = value;
    }

    @GroovyBlacklist
    public boolean isTestingStartingItems() {
        return testingStartingItems;
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        givenItemsAnySlot.clear();
        givenItemsSlots.clear();
    }

    @Override
    @GroovyBlacklist
    public void afterScriptLoad() {}
}
