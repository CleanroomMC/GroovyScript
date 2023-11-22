package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.internal.WeightedRandomLoot;

import java.util.ArrayList;
import java.util.List;

public class LootBag extends VirtualizedRegistry<LootBag.InternalLootbag> {

    public LootBag() {
        super();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(bag -> getLootbag(bag.getRarity()).removeIf(x -> x.item == bag.getItem() && x.itemWeight == bag.weight));
        restoreFromBackup().forEach(bag -> ThaumcraftApi.addLootBagItem(bag.getItem(), bag.getWeight(), bag.getRarity()));
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

    private static ArrayList<WeightedRandomLoot> getLootbag(int rarity) {
        switch (rarity) {
            case 0:
                return WeightedRandomLoot.lootBagCommon;
            case 1:
                return WeightedRandomLoot.lootBagUncommon;
            case 2:
                return WeightedRandomLoot.lootBagRare;
            default:
                GroovyLog.msg("Error: Thaumcraft Lootbag type not specified. Please use Lootbag.getCommon(), Lootbag.getUncommon(), or Lootbag.getRare().").error().post();
                return new ArrayList<>();
        }
    }

    public void add(ItemStack item, int chance, int rarity) {
        ThaumcraftApi.addLootBagItem(item, chance, rarity);
        addScripted(new InternalLootbag(item, chance, rarity));
    }

    public void remove(ItemStack item, int rarity) {
        ArrayList<WeightedRandomLoot> list = getLootbag(rarity);
        List<WeightedRandomLoot> remove = new ArrayList<>();
        for (WeightedRandomLoot loot : list) {
            if (item.isItemEqual(loot.item)) {
                remove.add(loot);
                addBackup(new InternalLootbag(loot.item, loot.itemWeight, rarity));
            }
        }
        list.removeAll(remove);
    }

    public void removeAll(int rarity) {
        ArrayList<WeightedRandomLoot> list = getLootbag(rarity);
        List<WeightedRandomLoot> remove = new ArrayList<>();
        for (WeightedRandomLoot loot : list) {
            remove.add(loot);
            addBackup(new InternalLootbag(loot.item, loot.itemWeight, rarity));
        }
        list.removeAll(remove);
    }

    public static class LootBagHelper {

        private final int rarity;

        public LootBagHelper(int rarity) {
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

    public static class InternalLootbag {

        private final ItemStack item;
        private final int weight;
        private final int rarity;

        private InternalLootbag(ItemStack item, int weight, int rarity) {
            this.item = item;
            this.weight = weight;
            this.rarity = rarity;
        }

        public ItemStack getItem() {
            return item;
        }

        public int getWeight() {
            return weight;
        }

        public int getRarity() {
            return rarity;
        }

    }

}
