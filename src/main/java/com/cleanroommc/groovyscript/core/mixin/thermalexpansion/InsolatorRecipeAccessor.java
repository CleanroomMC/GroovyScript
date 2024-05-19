package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.InsolatorManager;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = InsolatorManager.InsolatorRecipe.class, remap = false)
public interface InsolatorRecipeAccessor {

    @Invoker("<init>")
    static InsolatorManager.InsolatorRecipe createInsolatorRecipe(ItemStack secondaryInput, ItemStack primaryInput, ItemStack primaryOutput, ItemStack secondaryOutput, int secondaryChance, int energy, int water, InsolatorManager.Type type) {
        throw new UnsupportedOperationException();
    }

}
