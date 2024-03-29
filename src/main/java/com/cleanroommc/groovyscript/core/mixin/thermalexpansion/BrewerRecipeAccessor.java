package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.BrewerManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = BrewerManager.BrewerRecipe.class, remap = false)
public interface BrewerRecipeAccessor {

    @Invoker("<init>")
    static BrewerManager.BrewerRecipe createBrewerRecipe(ItemStack input, FluidStack inputFluid, FluidStack outputFluid, int energy) {
        throw new UnsupportedOperationException();
    }

}
