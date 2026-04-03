package com.cleanroommc.groovyscript.compat.mods.thaumcraft.aspect;

import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import net.minecraft.item.ItemStack;

public class AspectItemStackExpansion {

    public static void addAspect(ItemStack itemStack, AspectStack aspect) {
        ModSupport.THAUMCRAFT.get().aspectHelper.add(IngredientHelper.toIIngredient(itemStack), aspect);
    }

    public static void removeAspect(ItemStack itemStack, AspectStack aspect) {
        ModSupport.THAUMCRAFT.get().aspectHelper.remove(IngredientHelper.toIIngredient(itemStack), aspect);
    }

    public static void clearAspects(ItemStack itemStack) {
        ModSupport.THAUMCRAFT.get().aspectHelper.removeAll(itemStack);
    }
}
