package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.SawmillManager;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = SawmillManager.SawmillRecipe.class, remap = false)
public interface SawmillRecipeAccessor {

    @Invoker("<init>")
    static SawmillManager.SawmillRecipe createSawmillRecipe(ItemStack input, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, int energy) {
        throw new UnsupportedOperationException();
    }

}
