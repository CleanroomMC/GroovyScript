package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.CompactorManager;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = CompactorManager.CompactorRecipe.class, remap = false)
public interface CompactorRecipeAccessor {

    @Invoker("<init>")
    static CompactorManager.CompactorRecipe createCompactorRecipe(ItemStack input, ItemStack output, int energy) {
        throw new UnsupportedOperationException();
    }

}
