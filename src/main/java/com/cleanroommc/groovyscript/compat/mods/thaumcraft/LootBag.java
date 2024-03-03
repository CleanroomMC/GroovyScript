package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.internal.WeightedRandomLoot;

import java.util.ArrayList;
import java.util.List;

@RegistryDescription(
        isFullyDocumented = false,
        admonition = @Admonition(value = "groovyscript.wiki.thaumcraft.loot_bag.note", type = Admonition.Type.BUG, format = Admonition.Format.STANDARD)
)
public class LootBag extends VirtualizedRegistry<LootBag.InternalLootbag> {

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

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = {
            @Example("item('minecraft:diamond_block'), 100, 2"),
            @Example("item('minecraft:dirt'), 100, 0")
    })
    public void add(ItemStack item, int chance, int rarity) {
        ThaumcraftApi.addLootBagItem(item, chance, rarity);
        addScripted(new InternalLootbag(item, chance, rarity));
    }

    @MethodDescription(example = @Example("item('minecraft:ender_pearl'), 0"))
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

    @MethodDescription(example = @Example("2"))
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
