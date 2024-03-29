package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.FurnaceManager;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = FurnaceManager.FurnaceRecipe.class, remap = false)
public interface FurnaceRecipeAccessor {

    @Invoker("<init>")
    static FurnaceManager.FurnaceRecipe createFurnaceRecipe(ItemStack input, ItemStack output, int energy) {
        throw new UnsupportedOperationException();
    }

    @Invoker("<init>")
    static FurnaceManager.FurnaceRecipe createFurnaceRecipe(ItemStack input, ItemStack output, int energy, int creosote) {
        throw new UnsupportedOperationException();
    }

}
