package com.cleanroommc.groovyscript.compat.enderio;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.mixin.enderio.FluidFuelRegisterAccessor;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import crazypants.enderio.base.fluid.FluidFuelRegister;
import crazypants.enderio.base.fluid.IFluidCoolant;
import crazypants.enderio.base.fluid.IFluidFuel;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class CombustionGen {

    public void addFuel(FluidStack fluidStack, int rfPerCycle, int totalBurnTime) {
        if (fluidStack == null) {
            GroovyLog.LOG.error("Error adding EnderIO fuel for null fluidstack!");
            return;
        }
        addFuel(fluidStack.getFluid(), rfPerCycle, totalBurnTime);
    }

    public void addFuel(Fluid fluid, int rfPerCycle, int totalBurnTime) {
        if (fluid == null) {
            GroovyLog.LOG.error("Error adding EnderIO fuel for null fluid!");
            return;
        }
        IFluidFuel existingFuel = findFuel(fluid);
        if (existingFuel != null) {
            ReloadableRegistryManager.addRecipeForRecovery(IFluidFuel.class, existingFuel);
        }
        FluidFuelRegister.instance.addFuel(fluid, rfPerCycle, totalBurnTime);
        ReloadableRegistryManager.markScriptRecipe(IFluidFuel.class, findFuel(fluid));
    }

    public void addCoolant(FluidStack fluidStack, float degreesPerMb) {
        if (fluidStack == null) {
            GroovyLog.LOG.error("Error adding EnderIO coolant for null fluidstack!");
            return;
        }
        addCoolant(fluidStack.getFluid(), degreesPerMb);
    }

    public void addCoolant(Fluid fluid, float degreesPerMb) {
        if (fluid == null) {
            GroovyLog.LOG.error("Error adding EnderIO coolant for null fluid!");
            return;
        }
        IFluidCoolant existingCoolant = findCoolant(fluid);
        if (existingCoolant != null) {
            ReloadableRegistryManager.addRecipeForRecovery(IFluidCoolant.class, existingCoolant);
        }
        FluidFuelRegister.instance.addCoolant(fluid, degreesPerMb);
        ReloadableRegistryManager.markScriptRecipe(IFluidFuel.class, findCoolant(fluid));
    }

    public void removeFuel(FluidStack fluidStack) {
        if (fluidStack == null) {
            GroovyLog.LOG.error("Error removing EnderIO fuel for null fluidstack!");
            return;
        }
        removeFuel(fluidStack.getFluid());
    }

    public void removeFuel(Fluid fluid) {
        if (fluid == null) {
            GroovyLog.LOG.error("Error removing EnderIO fuel for null fluid!");
            return;
        }
        IFluidFuel existingFuel = findFuel(fluid);
        if (existingFuel != null) {
            ReloadableRegistryManager.addRecipeForRecovery(IFluidFuel.class, existingFuel);
            ((FluidFuelRegisterAccessor) FluidFuelRegister.instance).getFuels().remove(fluid.getName());
        } else {
            GroovyLog.LOG.error("No EnderIO fuel found for {} fluid!", fluid.getName());
        }
    }

    public void removeCoolant(FluidStack fluidStack) {
        if (fluidStack == null) {
            GroovyLog.LOG.error("Error removing EnderIO coolant for null fluidstack!");
            return;
        }
        removeCoolant(fluidStack.getFluid());
    }

    public void removeCoolant(Fluid fluid) {
        if (fluid == null) {
            GroovyLog.LOG.error("Error removing EnderIO coolant for null fluid!");
            return;
        }
        IFluidCoolant existingCoolant = findCoolant(fluid);
        if (existingCoolant != null) {
            ReloadableRegistryManager.addRecipeForRecovery(IFluidCoolant.class, existingCoolant);
            ((FluidFuelRegisterAccessor) FluidFuelRegister.instance).getCoolants().remove(fluid.getName());
        } else {
            GroovyLog.LOG.error("No EnderIO coolant found for {} fluid!", fluid.getName());
        }
    }

    @Nullable
    public IFluidFuel findFuel(Fluid fluid) {
        return FluidFuelRegister.instance.getFuel(fluid);
    }

    @Nullable
    public IFluidCoolant findCoolant(Fluid fluid) {
        return FluidFuelRegister.instance.getCoolant(fluid);
    }

    @GroovyBlacklist
    public void onReload() {
        FluidFuelRegisterAccessor accessor = (FluidFuelRegisterAccessor) FluidFuelRegister.instance;
        ReloadableRegistryManager.unmarkScriptRecipes(IFluidCoolant.class).forEach(accessor.getCoolants()::remove);
        ReloadableRegistryManager.unmarkScriptRecipes(IFluidFuel.class).forEach(accessor.getFuels()::remove);
        ReloadableRegistryManager.recoverRecipes(IFluidCoolant.class)
                .stream()
                .map(IFluidCoolant.class::cast)
                .forEach(coolant -> accessor.getCoolants().put(coolant.getFluid().getName(), coolant));
        ReloadableRegistryManager.recoverRecipes(IFluidFuel.class)
                .stream()
                .map(IFluidFuel.class::cast)
                .forEach(fuel -> accessor.getFuels().put(fuel.getFluid().getName(), fuel));
    }

}
