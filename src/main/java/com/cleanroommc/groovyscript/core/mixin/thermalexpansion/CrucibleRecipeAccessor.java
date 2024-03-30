package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.CrucibleManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = CrucibleManager.CrucibleRecipe.class, remap = false)
public interface CrucibleRecipeAccessor {

    @Invoker("<init>")
    static CrucibleManager.CrucibleRecipe createCrucibleRecipe(ItemStack input, FluidStack output, int energy) {
        throw new UnsupportedOperationException();
    }

}
