package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.RefineryManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = RefineryManager.RefineryRecipe.class, remap = false)
public interface RefineryRecipeAccessor {

    @Invoker("<init>")
    static RefineryManager.RefineryRecipe createRefineryRecipe(FluidStack input, FluidStack outputFluid, ItemStack outputItem, int energy, int chance) {
        throw new UnsupportedOperationException();
    }

}
