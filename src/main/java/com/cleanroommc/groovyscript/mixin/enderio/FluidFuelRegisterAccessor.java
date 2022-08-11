package com.cleanroommc.groovyscript.mixin.enderio;

import crazypants.enderio.base.fluid.FluidFuelRegister;
import crazypants.enderio.base.fluid.IFluidCoolant;
import crazypants.enderio.base.fluid.IFluidFuel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(FluidFuelRegister.class)
public interface FluidFuelRegisterAccessor {

    @Accessor
    Map<String, IFluidCoolant> getCoolants();

    @Accessor
    Map<String, IFluidFuel> getFuels();

}
