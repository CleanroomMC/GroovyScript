package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.EnchanterManager;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = EnchanterManager.EnchanterRecipe.class, remap = false)
public interface EnchanterRecipeAccessor {

    @Invoker("<init>")
    static EnchanterManager.EnchanterRecipe createEnchanterRecipe(ItemStack primaryInput, ItemStack secondaryInput, ItemStack output, int experience, int energy, EnchanterManager.Type type) {
        throw new UnsupportedOperationException();
    }

}
