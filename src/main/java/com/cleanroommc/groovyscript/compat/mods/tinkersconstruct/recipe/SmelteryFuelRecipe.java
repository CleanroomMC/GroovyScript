package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.recipe;

import net.minecraftforge.fluids.FluidStack;

import java.util.Map;

@SuppressWarnings("ClassCanBeRecord")
public class SmelteryFuelRecipe {

    public final FluidStack fluid;
    public final int duration;

    public SmelteryFuelRecipe(FluidStack fluid, int duration) {
        this.fluid = fluid;
        this.duration = duration;
    }

    public static SmelteryFuelRecipe fromMapEntry(Map.Entry<FluidStack, Integer> entry) {
        return new SmelteryFuelRecipe(entry.getKey(), entry.getValue());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof SmelteryFuelRecipe other)) return false;

        if (other.duration != duration) return false;
        return !other.fluid.isFluidStackIdentical(fluid);
    }
}
