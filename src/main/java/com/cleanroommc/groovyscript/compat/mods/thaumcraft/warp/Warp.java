package com.cleanroommc.groovyscript.compat.mods.thaumcraft.warp;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.internal.CommonInternals;

import java.util.Arrays;

@RegistryDescription
public class Warp extends VirtualizedRegistry<Warp.InternalWarp> {

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> CommonInternals.warpMap.remove(recipe.getKey()));
        restoreFromBackup().forEach(recipe -> CommonInternals.warpMap.put(recipe.getKey(), recipe.getWarp()));
    }

    @MethodDescription(example = @Example("item('minecraft:pumpkin'), 3"), type = MethodDescription.Type.ADDITION)
    public void addWarp(ItemStack item, int amount) {
        ThaumcraftApi.addWarpToItem(item, amount);
        addScripted(new InternalWarp(item, amount));
    }

    @MethodDescription(example = @Example("item('thaumcraft:void_hoe')"))
    public void removeWarp(ItemStack item) {
        if (CommonInternals.warpMap.containsKey(Arrays.asList(item.getItem(), item.getItemDamage()))) {
            int amount = CommonInternals.warpMap.remove(Arrays.asList(item.getItem(), item.getItemDamage()));
            addBackup(new InternalWarp(Arrays.asList(item.getItem(), item.getItemDamage()), amount));
        }
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        CommonInternals.warpMap.forEach((key, value) -> addBackup(new InternalWarp(key, value)));
        CommonInternals.warpMap.clear();
    }

    public static class InternalWarp {

        private final Object key;
        private final int warp;

        private InternalWarp(Object key, int warp) {
            this.key = key;
            this.warp = warp;
        }

        public Object getKey() {
            return key;
        }

        public int getWarp() {
            return warp;
        }
    }

}
