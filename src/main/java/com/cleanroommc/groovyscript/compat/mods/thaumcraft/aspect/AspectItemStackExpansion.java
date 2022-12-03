package com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect;

import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;

public class AspectItemStackExpansion {

    public static void addAspect(ItemStack itemStack, AspectStack aspect) {
        AspectList aspectList = AspectHelper.getObjectAspects(itemStack);
        aspectList.add(aspect.getAspect(), aspect.getAmount());
    }

    public static void removeAspect(ItemStack itemStack, AspectStack aspect) {
        AspectList aspectList = AspectHelper.getObjectAspects(itemStack);
        aspectList.remove(aspect.getAspect());
    }

    public static void clearAspects(ItemStack itemStack) {
        ThaumcraftApi.registerObjectTag(itemStack, null);
    }
}
