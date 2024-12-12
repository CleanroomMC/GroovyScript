package com.cleanroommc.groovyscript.compat.mods.enderio;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.enderio.FluidFuelRegisterAccessor;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import crazypants.enderio.base.fluid.FluidFuelRegister;
import crazypants.enderio.base.fluid.IFluidCoolant;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@RegistryDescription
public class FluidCoolant extends VirtualizedRegistry<IFluidCoolant> {

    public FluidCoolant() {
        super(Alias.generateOfClassAnd(FluidCoolant.class, "CombustionCoolant"));
    }

    @MethodDescription(example = @Example("fluid('xpjuice'), 1000"), type = MethodDescription.Type.ADDITION)
    public void addCoolant(FluidStack fluidStack, float degreesPerMb) {
        if (fluidStack == null) {
            GroovyLog.get().error("Error adding EnderIO coolant for null fluidstack!");
            return;
        }
        addCoolant(fluidStack.getFluid(), degreesPerMb);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void addCoolant(Fluid fluid, float degreesPerMb) {
        if (fluid == null) {
            GroovyLog.get().error("Error adding EnderIO coolant for null fluid!");
            return;
        }
        IFluidCoolant coolant = new FluidFuelRegister.CoolantImpl(fluid, degreesPerMb);
        addCoolant(coolant);
    }

    public void addCoolant(IFluidCoolant fluidCoolant) {
        if (fluidCoolant == null) {
            GroovyLog.get().error("Error adding EnderIO coolant for null fluidCoolant!");
            return;
        }
        ((FluidFuelRegisterAccessor) FluidFuelRegister.instance).getCoolants().put(fluidCoolant.getFluid().getName(), fluidCoolant);
        addScripted(fluidCoolant);
    }

    public boolean remove(IFluidCoolant fluidCoolant) {
        if (fluidCoolant == null) {
            GroovyLog.get().error("Error removing EnderIO coolant for null fluidCoolant!");
            return false;
        }
        remove(fluidCoolant.getFluid());
        return true;
    }

    @MethodDescription(example = @Example("fluid('water')"))
    public void remove(FluidStack fluidStack) {
        if (fluidStack == null) {
            GroovyLog.get().error("Error removing EnderIO coolant for null fluidstack!");
            return;
        }
        remove(fluidStack.getFluid());
    }

    @MethodDescription
    public void remove(Fluid fluid) {
        if (fluid == null) {
            GroovyLog.get().error("Error removing EnderIO coolant for null fluid!");
            return;
        }
        IFluidCoolant existingCoolant = find(fluid);
        if (existingCoolant != null) {
            addBackup(existingCoolant);
            ((FluidFuelRegisterAccessor) FluidFuelRegister.instance).getCoolants().remove(fluid.getName());
        } else {
            GroovyLog.get().error("No EnderIO coolant found for {} fluid!", fluid.getName());
        }
    }

    public @Nullable IFluidCoolant find(Fluid fluid) {
        return ((FluidFuelRegisterAccessor) FluidFuelRegister.instance).getCoolants().get(fluid.getName());
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        FluidFuelRegisterAccessor accessor = (FluidFuelRegisterAccessor) FluidFuelRegister.instance;
        removeScripted().forEach(c -> accessor.getCoolants().remove(c.getFluid().getName()));
        restoreFromBackup().forEach(c -> accessor.getCoolants().put(c.getFluid().getName(), c));
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<Map.Entry<String, IFluidCoolant>> streamRecipes() {
        return new SimpleObjectStream<>(((FluidFuelRegisterAccessor) FluidFuelRegister.instance).getCoolants().entrySet())
                .setRemover(r -> remove(r.getValue()));
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ((FluidFuelRegisterAccessor) FluidFuelRegister.instance).getCoolants().forEach((r, l) -> {
            if (l == null) return;
            addBackup(l);
        });
        ((FluidFuelRegisterAccessor) FluidFuelRegister.instance).getCoolants().clear();
    }
}
