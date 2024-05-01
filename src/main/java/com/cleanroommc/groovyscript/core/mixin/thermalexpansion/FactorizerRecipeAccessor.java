package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.thermalexpansion.util.managers.device.FactorizerManager;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = FactorizerManager.FactorizerRecipe.class, remap = false)
public interface FactorizerRecipeAccessor {

    @Invoker("<init>")
    static FactorizerManager.FactorizerRecipe createFactorizerRecipe(ItemStack input, ItemStack output) {
        throw new UnsupportedOperationException();
    }

}
