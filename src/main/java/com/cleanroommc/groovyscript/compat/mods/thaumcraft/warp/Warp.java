package com.cleanroommc.groovyscript.compat.mods.thaumcraft.warp;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.internal.CommonInternals;

import java.util.ArrayList;
import java.util.Arrays;

public class Warp extends VirtualizedRegistry<ArrayList<Object>> {

    public Warp() {
        super();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> this.removeWarp((ItemStack) recipe.get(0)));
        restoreFromBackup().forEach(recipe -> this.addWarp((ItemStack) recipe.get(0), (int) recipe.get(1)));
    }

    public void addWarp(ItemStack item, int amount) {
        ThaumcraftApi.addWarpToItem(item, amount);
        ArrayList<Object> warp = new ArrayList<>();
        warp.add(item);
        warp.add(amount);
        addScripted(warp);
    }

    public void removeWarp(ItemStack item) {
        if (CommonInternals.warpMap.containsKey(Arrays.asList(item.getItem(), item.getItemDamage()))) {
            int amount = CommonInternals.warpMap.remove(Arrays.asList(item.getItem(), item.getItemDamage()));
            ArrayList<Object> warp = new ArrayList<>();
            warp.add(item);
            warp.add(amount);
            addBackup(warp);
        }
    }

}
