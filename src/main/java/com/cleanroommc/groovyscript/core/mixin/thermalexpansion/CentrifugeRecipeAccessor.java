package com.cleanroommc.groovyscript.core.mixin.thermalexpansion;

import cofh.thermalexpansion.util.managers.machine.CentrifugeManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(value = CentrifugeManager.CentrifugeRecipe.class, remap = false)
public interface CentrifugeRecipeAccessor {

    @Invoker("<init>")
    static CentrifugeManager.CentrifugeRecipe createCentrifugeRecipe(ItemStack input,
                                                                     @Nullable List<ItemStack> output,
                                                                     @Nullable List<Integer> chance, @Nullable FluidStack fluid, int energy) {
        throw new UnsupportedOperationException();
    }

}
