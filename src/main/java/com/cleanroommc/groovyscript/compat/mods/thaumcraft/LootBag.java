package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.internal.WeightedRandomLoot;

import java.util.*;

public class LootBag {

    public void addItem(ItemStack item, int chance, int[] bags) {
        ThaumcraftApi.addLootBagItem(item, chance, bags);
    }

    public void removeItem(ItemStack item, int[] bags) {
        for (int i : bags){
            ArrayList<WeightedRandomLoot> list = new ArrayList<WeightedRandomLoot>();
            switch (i) {
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
                    GroovyLog.msg("Error: Thaumcraft Lootbag type out of bounds, [0-2] applicable.").error().post();
            }
            List<WeightedRandomLoot> remove = new ArrayList<>();
            for (WeightedRandomLoot loot : list) {
                if (item.isItemEqual(loot.item))
                    remove.add(loot);
            }
            list.removeAll(remove);
        }
    }

    public void removeAll(int[] bags) {
        for (int i : bags){
            ArrayList<WeightedRandomLoot> list = new ArrayList<WeightedRandomLoot>();
            switch (i) {
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
                    GroovyLog.msg("Error: Thaumcraft Lootbag type out of bounds, [0-2] applicable.").error().post();
            }
            List<WeightedRandomLoot> remove = new ArrayList<>();
            for (WeightedRandomLoot loot : list)
                remove.add(loot);
            list.removeAll(remove);
        }
    }
}
