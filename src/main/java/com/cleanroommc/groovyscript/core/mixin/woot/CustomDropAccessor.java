package com.cleanroommc.groovyscript.core.mixin.woot;

import ipsis.woot.util.EnumEnchantKey;
import ipsis.woot.util.WootMobName;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;

@Mixin(targets = "ipsis.woot.loot.customdrops.CustomDropsRepository$CustomDrop", remap = false)
public interface CustomDropAccessor {

    @Accessor
    WootMobName getWootMobName();

    @Accessor
    ItemStack getItemStack();

    @Accessor
    HashMap<EnumEnchantKey, Integer> getChanceMap();

    @Accessor
    HashMap<EnumEnchantKey, Integer> getSizeMap();

}
