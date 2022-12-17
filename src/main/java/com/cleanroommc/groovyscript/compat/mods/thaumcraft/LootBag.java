package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.internal.WeightedRandomLoot;

import java.util.*;

public class LootBag extends VirtualizedRegistry<ArrayList<Object>> {

    public LootBag() {
        super();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(bag -> this.remove((ItemStack) bag.get(1), (int) bag.get(0)));
        restoreFromBackup().forEach(bag -> this.add((ItemStack) bag.get(1), (int) bag.get(2), (int) bag.get(0)));
    }

    public static LootBagHelper getCommon() {
        return new LootBagHelper(0);
    }

    public static LootBagHelper getUncommon() {
        return new LootBagHelper(1);
    }

    public static LootBagHelper getRare() {
        return new LootBagHelper(2);
    }

    public void add(ItemStack item, int chance, int rarity) {
        ThaumcraftApi.addLootBagItem(item, chance, rarity);
        ArrayList<Object> bag = new ArrayList<>();
        bag.add(rarity);
        bag.add(item);
        bag.add(chance);
        addScripted(bag);
    }

    public void remove(ItemStack item, int rarity) {
        ArrayList<WeightedRandomLoot> list = new ArrayList<>();
        switch (rarity) {
            case 0:
                list = WeightedRandomLoot.lootBagCommon;
                break;
            case 1:
                list = WeightedRandomLoot.lootBagUncommon;
                break;
            case 2:
                list = WeightedRandomLoot.lootBagRare;
                break;
            default:
                GroovyLog.msg("Error: Thaumcraft Lootbag type not specified. Please use Lootbag.getCommon(), Lootbag.getUncommon(), or Lootbag.getRare().").error().post();
        }
        List<WeightedRandomLoot> remove = new ArrayList<>();
        for (WeightedRandomLoot loot : list) {
            if (item.isItemEqual(loot.item)) {
                remove.add(loot);
                ArrayList<Object> bag = new ArrayList<>();
                bag.add(rarity);
                bag.add(loot.item);
                bag.add(loot.itemWeight);
                addBackup(bag);
            }
        }
        list.removeAll(remove);
    }

    public void removeAll(int rarity) {
        ArrayList<WeightedRandomLoot> list = new ArrayList<>();
        switch (rarity) {
            case 0:
                list = WeightedRandomLoot.lootBagCommon;
                break;
            case 1:
                list = WeightedRandomLoot.lootBagUncommon;
                break;
            case 2:
                list = WeightedRandomLoot.lootBagRare;
                break;
            default:
                GroovyLog.msg("Error: Thaumcraft Lootbag type not specified. Please use Lootbag.getCommon(), Lootbag.getUncommon(), or Lootbag.getRare().").error().post();
        }
        List<WeightedRandomLoot> remove = new ArrayList<>();
        for (WeightedRandomLoot loot : list) {
            remove.add(loot);
            ArrayList<Object> bag = new ArrayList<>();
            bag.add(rarity);
            bag.add(loot.item);
            bag.add(loot.itemWeight);
            addBackup(bag);
        }
        list.removeAll(remove);
    }

    public static class LootBagHelper {

        private final int rarity;

        public LootBagHelper (int rarity) {
            this.rarity = rarity;
        }

        public void addItem(ItemStack item, int chance) {
            ModSupport.THAUMCRAFT.get().lootBag.add(item, chance, rarity);
        }

        public void removeItem(ItemStack item) {
            ModSupport.THAUMCRAFT.get().lootBag.remove(item, rarity);
        }

        public void removeAll() {
            ModSupport.THAUMCRAFT.get().lootBag.removeAll(rarity);
        }
    }
}
