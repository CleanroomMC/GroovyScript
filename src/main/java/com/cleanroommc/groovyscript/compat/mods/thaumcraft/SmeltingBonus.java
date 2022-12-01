package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.internal.CommonInternals;

import java.util.ArrayList;
import java.util.List;

public class SmeltingBonus {

    public SmeltingBonus() {
        //do nothing
    }

    public void add(ItemStack in, ItemStack out) {
        ThaumcraftApi.addSmeltingBonus(in, out);
    }

    public void add(ItemStack in, ItemStack out, float chance) {
        ThaumcraftApi.addSmeltingBonus(in, out, chance);
    }

    public void removeByOutput(ItemStack output) {
        List<ThaumcraftApi.SmeltBonus> remove = new ArrayList<>();
        for (ThaumcraftApi.SmeltBonus bonus : CommonInternals.smeltingBonus)
            if (output.isItemEqual(bonus.out))
                remove.add(bonus);
        CommonInternals.smeltingBonus.removeAll(remove);
    }

    public SmeltingBonusBuilder recipeBuilder() { return new SmeltingBonusBuilder(); }

    public class SmeltingBonusBuilder {

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
            ThaumcraftApi.addSmeltingBonus(in, out, chance);
        }
    }
}
