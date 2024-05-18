package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.PulverizerManager;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = PulverizerManager.PulverizerRecipe.class, remap = false)
public interface PulverizerRecipeAccessor {

    @Invoker("<init>")
    static PulverizerManager.PulverizerRecipe createPulverizerRecipe(ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, int energy) {
        throw new UnsupportedOperationException();
    }

}
