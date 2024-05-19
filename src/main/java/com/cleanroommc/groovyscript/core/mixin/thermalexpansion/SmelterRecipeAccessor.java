package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.SmelterManager;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = SmelterManager.SmelterRecipe.class, remap = false)
public interface SmelterRecipeAccessor {

    @Invoker("<init>")
    static SmelterManager.SmelterRecipe createSmelterRecipe(ItemStack secondaryInput, ItemStack primaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, int energy) {
        throw new UnsupportedOperationException();
    }

}
