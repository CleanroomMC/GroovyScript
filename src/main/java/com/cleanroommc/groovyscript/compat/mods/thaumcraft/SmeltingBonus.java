package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.internal.CommonInternals;

import java.util.ArrayList;
import java.util.List;

public class SmeltingBonus {

    public static void add(ItemStack in, ItemStack out) {
        ThaumcraftApi.addSmeltingBonus(in, out);
    }

    public static void add(ItemStack in, ItemStack out, float chance) {
        ThaumcraftApi.addSmeltingBonus(in, out, chance);
    }

    public static void removeByOutput(ItemStack output) {
        List<ThaumcraftApi.SmeltBonus> remove = new ArrayList<>();
        for (ThaumcraftApi.SmeltBonus bonus : CommonInternals.smeltingBonus)
            if (output.isItemEqual(bonus.out))
                remove.add(bonus);
        CommonInternals.smeltingBonus.removeAll(remove);
    }

    public static class SmeltingBonusBuilder {

        private ItemStack in;
        private ItemStack out;
        private float chance = 0.33F;

        public SmeltingBonusBuilder input(ItemStack input) {
            this.in = input;
            return this;
        }

        public SmeltingBonusBuilder output(ItemStack output) {
            this.out = output;
            return this;
        }

        public SmeltingBonusBuilder chance(float chance) {
            this.chance = chance;
            return this;
        }

        public void register() {
            add(in, out, chance);
        }

    }

}
