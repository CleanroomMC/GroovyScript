package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.ExtruderManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ExtruderManager.ExtruderRecipe.class, remap = false)
public interface ExtruderRecipeAccessor {

    @Invoker("<init>")
    static ExtruderManager.ExtruderRecipe createExtruderRecipe(ItemStack output, FluidStack inputHot, FluidStack inputCold, int energy) {
        throw new UnsupportedOperationException();
    }

}
