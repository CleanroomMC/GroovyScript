package com.cleanroommc.groovyscript.core.mixin.ic2;

import ic2.api.recipe.ISemiFluidFuelManager;
import ic2.core.SemiFluidFuelManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SemiFluidFuelManager.class)
public interface SemiFluidFuelManagerAccessor {

    @Accessor
    Map<String, ISemiFluidFuelManager.FuelProperty> getFuelProperties();
}
