package com.cleanroommc.groovyscript.compat.mods.enderio;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.core.mixin.enderio.FluidFuelRegisterAccessor;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import crazypants.enderio.base.fluid.FluidFuelRegister;
import crazypants.enderio.base.fluid.IFluidFuel;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class FluidFuel extends VirtualizedRegistry<IFluidFuel> {

    public FluidFuel() {
        super(Alias.generateOf("CombustionFuel"));
    }

    public void addFuel(FluidStack fluidStack, int rfPerCycle, int totalBurnTime) {
        if (fluidStack == null) {
            GroovyLog.get().error("Error adding EnderIO fuel for null fluidstack!");
            return;
        }
        addFuel(fluidStack.getFluid(), rfPerCycle, totalBurnTime);
    }

    public void addFuel(Fluid fluid, int rfPerCycle, int totalBurnTime) {
        if (fluid == null) {
            GroovyLog.get().error("Error adding EnderIO fuel for null fluid!");
            return;
        }
        IFluidFuel existingFuel = find(fluid);
        if (existingFuel != null) {
            addBackup(existingFuel);
        }
        FluidFuelRegister.instance.addFuel(fluid, rfPerCycle, totalBurnTime);
        addScripted(find(fluid));
    }

    public boolean remove(IFluidFuel fluidFuel) {
        if (fluidFuel == null) {
            GroovyLog.get().error("Error removing EnderIO coolant for null fluidFuel!");
            return false;
        }
        remove(fluidFuel.getFluid());
        return true;
    }

    public void remove(FluidStack fluidStack) {
        if (fluidStack == null) {
            GroovyLog.get().error("Error removing EnderIO fuel for null fluidstack!");
            return;
        }
        remove(fluidStack.getFluid());
    }

    public void remove(Fluid fluid) {
        if (fluid == null) {
            GroovyLog.get().error("Error removing EnderIO fuel for null fluid!");
            return;
        }
        IFluidFuel existingFuel = find(fluid);
        if (existingFuel != null) {
            addBackup(existingFuel);
            ((FluidFuelRegisterAccessor) FluidFuelRegister.instance).getFuels().remove(fluid.getName());
        } else {
            GroovyLog.get().error("No EnderIO fuel found for {} fluid!", fluid.getName());
        }
    }

    @Nullable
    public IFluidFuel find(Fluid fluid) {
        return FluidFuelRegister.instance.getFuel(fluid);
    }

    @GroovyBlacklist
    public void onReload() {
        FluidFuelRegisterAccessor accessor = (FluidFuelRegisterAccessor) FluidFuelRegister.instance;
        removeScripted().forEach(c -> accessor.getFuels().remove(c.getFluid().getName()));
        restoreFromBackup().forEach(c -> accessor.getFuels().put(c.getFluid().getName(), c));
    }

    public SimpleObjectStream<Map.Entry<String, IFluidFuel>> streamRecipes() {
        return new SimpleObjectStream<>(((FluidFuelRegisterAccessor) FluidFuelRegister.instance).getFuels().entrySet())
                .setRemover(r -> remove(r.getValue()));
    }

    public void removeAll() {
        ((FluidFuelRegisterAccessor) FluidFuelRegister.instance).getFuels().forEach((r, l) -> {
            if (l == null) return;
            addBackup(l);
        });
        ((FluidFuelRegisterAccessor) FluidFuelRegister.instance).getFuels().clear();
    }

}
