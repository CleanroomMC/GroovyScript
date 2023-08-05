package com.cleanroommc.groovyscript.compat.vanilla;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class RarityItemStackExpansion {

    public static ItemStack setRarity(ItemStack item, TextFormatting color) {
        VanillaModule.rarity.set(color, item);
        return item;
    }
}
