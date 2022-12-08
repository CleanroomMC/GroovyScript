package com.cleanroommc.groovyscript.compat.mods.thaumcraft.warp;

import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect.AspectStack;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;

public class WarpItemStackExpansion {

    public static void addWarp(ItemStack itemStack, int warp) { ModSupport.THAUMCRAFT.get().warp.addWarp(itemStack, warp); }

    public static void clearWarp(ItemStack itemStack) { ModSupport.THAUMCRAFT.get().warp.removeWarp(itemStack); }

}
