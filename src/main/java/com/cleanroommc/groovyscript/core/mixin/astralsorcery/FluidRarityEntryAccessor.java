package com.cleanroommc.groovyscript.core.mixin.astralsorcery;

import hellfirepvp.astralsorcery.common.base.FluidRarityRegistry;
import net.minecraftforge.fluids.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FluidRarityRegistry.FluidRarityEntry.class)
public interface FluidRarityEntryAccessor {

    @Invoker("<init>")
    static FluidRarityRegistry.FluidRarityEntry createFluidRarityEntry(String fluidNameTmp, int rarity, int guaranteedAmount, int additionalRandomAmount) {
        throw new UnsupportedOperationException();
    }

    @Invoker("<init>")
    static FluidRarityRegistry.FluidRarityEntry createFluidRarityEntry(Fluid fluid, int rarity, int guaranteedAmount, int additionalRandomAmount) {
        throw new UnsupportedOperationException();
    }
}
