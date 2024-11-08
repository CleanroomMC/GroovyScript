package com.cleanroommc.groovyscript.core.mixin.pneumaticcraft;

import me.desht.pneumaticcraft.common.recipes.PlasticMixerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = PlasticMixerRegistry.PlasticMixerRecipe.class, remap = false)
public interface PlasticMixerRecipeAccessor {

    @Invoker("<init>")
    static PlasticMixerRegistry.PlasticMixerRecipe createPlasticMixerRecipe(FluidStack fluidStack, ItemStack itemStack, int temperature, boolean allowMelting, boolean allowSolidifying, boolean useDye, int meta) {
        throw new UnsupportedOperationException();
    }
}
