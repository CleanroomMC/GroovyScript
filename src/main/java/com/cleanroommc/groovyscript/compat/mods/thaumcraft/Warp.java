package com.cleanroommc.groovyscript.compat.mods.thaumcraft;

import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.internal.CommonInternals;

import java.util.Arrays;

public class Warp {

    public Warp() {
        //do nothing
    }

    public void addWarp(ItemStack item, int amount) {
        ThaumcraftApi.addWarpToItem(item, amount);
    }

    public void removeWarp(ItemStack item) {
        if (CommonInternals.warpMap.containsKey(Arrays.asList(item.getItem(), item.getItemDamage())))
            CommonInternals.warpMap.remove(Arrays.asList(item.getItem(), item.getItemDamage()));
    }

}
