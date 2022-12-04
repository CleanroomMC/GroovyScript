package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.internal.WeightedRandomLoot;

import java.util.*;

public class LootBag {

    private int type;

    public LootBag() {
    }

    public LootBag(int type) {
        this.type = type;
    }

    public static LootBag getCommon() {
        return new LootBag(0);
    }

    public static LootBag getUncommon() {
        return new LootBag(1);
    }

    public static LootBag getRare() {
        return new LootBag(2);
    }

    public void addItem(ItemStack item, int chance) {
        ThaumcraftApi.addLootBagItem(item, chance, new int[]{type});
    }

    public void removeItem(ItemStack item) {
        ArrayList<WeightedRandomLoot> list = new ArrayList<WeightedRandomLoot>();
        switch (type) {
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
            if (item.isItemEqual(loot.item))
                remove.add(loot);
        }
        list.removeAll(remove);
    }

    public void removeAll() {
        ArrayList<WeightedRandomLoot> list = new ArrayList<WeightedRandomLoot>();
        switch (type) {
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
        for (WeightedRandomLoot loot : list)
            remove.add(loot);
        list.removeAll(remove);
    }
}
