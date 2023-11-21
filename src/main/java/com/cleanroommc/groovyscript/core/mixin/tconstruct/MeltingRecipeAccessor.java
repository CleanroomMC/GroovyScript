package com.cleanroommc.groovyscript.core.mixin.tconstruct;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;

@Mixin(value = MeltingRecipe.class, remap = false)
public interface MeltingRecipeAccessor {

    @Invoker
    static int invokeCalcTemperature(int temp, int timeAmount) {
        throw new AssertionError();
    }
}
