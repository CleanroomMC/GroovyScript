package com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect;

import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;

public class AspectItemStackExpansion {

    public static void addAspect(ItemStack itemStack, AspectStack aspect) {
        ModSupport.THAUMCRAFT.get().aspectHelper.add(itemStack, aspect);
    }

    public static void removeAspect(ItemStack itemStack, AspectStack aspect) {
        ModSupport.THAUMCRAFT.get().aspectHelper.remove(itemStack, aspect);
    }

    public static void clearAspects(ItemStack itemStack) {
        ModSupport.THAUMCRAFT.get().aspectHelper.removeAll(itemStack);
    }
}
