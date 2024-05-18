package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.PrecipitatorManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = PrecipitatorManager.PrecipitatorRecipe.class, remap = false)
public interface PrecipitatorRecipeAccessor {

    @Invoker("<init>")
    static PrecipitatorManager.PrecipitatorRecipe createPrecipitatorRecipe(ItemStack output, FluidStack input, int energy) {
        throw new UnsupportedOperationException();
    }

}
