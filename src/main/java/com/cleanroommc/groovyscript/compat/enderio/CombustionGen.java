package com.cleanroommc.groovyscript.compat.enderio;

import com.cleanroommc.groovyscript.compat.enderio.recipe.IEnderIOFuelRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import crazypants.enderio.base.fluid.FluidFuelRegister;
import net.minecraftforge.fluids.FluidStack;

public class CombustionGen {

    public void addFuel(FluidStack fluid, int rfPerCycle, int totalBurnTime) {
        if (fluid == null) {
            GroovyLog.LOG.error("Error adding EnderIO fuel for null fluid!");
            return;
        }
        FluidFuelRegister.instance.addFuel(fluid.getFluid(), rfPerCycle, totalBurnTime);
    }

    public void addCoolant(FluidStack fluid, float degreesPerMb) {
        if (fluid == null) {
            GroovyLog.LOG.error("Error adding EnderIO coolant for null fluid!");
            return;
        }
        FluidFuelRegister.instance.addCoolant(fluid.getFluid(), degreesPerMb);
    }

    public void removeFuel(FluidStack fluid) {
        if (fluid == null) {
            GroovyLog.LOG.error("Error removing EnderIO fuel for null fluid!");
            return;
        }
        ((IEnderIOFuelRegistry) FluidFuelRegister.instance).removeFuel(fluid.getFluid());
    }

    public void removeCoolant(FluidStack fluid) {
        if (fluid == null) {
            GroovyLog.LOG.error("Error removing EnderIO coolant for null fluid!");
            return;
        }
        ((IEnderIOFuelRegistry) FluidFuelRegister.instance).removeCoolant(fluid.getFluid());
    }
}
