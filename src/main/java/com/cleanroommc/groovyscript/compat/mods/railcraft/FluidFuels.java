package com.cleanroommc.groovyscript.compat.mods.railcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@RegistryDescription
public class FluidFuels extends VirtualizedRegistry<FluidFuels.FuelEntry> {

    private final Map<String, Integer> backupFuels = new HashMap<>();

    @Override
    public void onReload() {
        // Restore from backup
        restoreFromBackup().forEach(entry -> {
            try {
                mods.railcraft.api.fuel.FluidFuelManager.addFuel(entry.fluid, entry.heatValue);
            } catch (Exception e) {
                GroovyLog.msg("Error restoring Railcraft Fluid Fuel")
                        .add("{}", e.getMessage())
                        .error()
                        .post();
            }
        });
        // Remove scripted
        removeScripted().forEach(entry -> {
            try {
                // Note: Railcraft doesn't have a direct remove method, 
                // so we rely on the internal map being modified
                backupFuels.put(entry.fluid.getFluid().getName(), entry.heatValue);
            } catch (Exception e) {
                GroovyLog.msg("Error removing Railcraft Fluid Fuel")
                        .add("{}", e.getMessage())
                        .error()
                        .post();
            }
        });
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("fluid('lava'), 32000"))
    public void add(FluidStack fuel, int heatValue) {
        if (IngredientHelper.isEmpty(fuel)) {
            GroovyLog.msg("Error adding Railcraft Fluid Fuel")
                    .add("fuel must not be empty")
                    .error()
                    .post();
            return;
        }
        if (heatValue <= 0) {
            GroovyLog.msg("Error adding Railcraft Fluid Fuel")
                    .add("heat value must be greater than 0")
                    .error()
                    .post();
            return;
        }
        
        addScripted(new FuelEntry(fuel.copy(), heatValue));
        
        try {
            mods.railcraft.api.fuel.FluidFuelManager.addFuel(fuel, heatValue);
        } catch (Exception e) {
            GroovyLog.msg("Error adding Railcraft Fluid Fuel")
                    .add("{}", e.getMessage())
                    .error()
                    .post();
        }
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public void add(FluidStack fuel) {
        add(fuel, 32000); // Default heat value
    }

    @MethodDescription(example = @Example("fluid('creosote')"))
    public void remove(FluidStack fuel) {
        if (IngredientHelper.isEmpty(fuel)) {
            GroovyLog.msg("Error removing Railcraft Fluid Fuel")
                    .add("fuel must not be empty")
                    .error()
                    .post();
            return;
        }
        
        // Store current value for backup
        int currentValue = mods.railcraft.api.fuel.FluidFuelManager.getFuelValue(fuel);
        if (currentValue > 0) {
            addBackup(new FuelEntry(fuel.copy(), currentValue));
        }
        
        // Note: Railcraft's FluidFuelManager doesn't have a direct remove method
        // We can only add with 0 heat value to effectively disable it
        try {
            mods.railcraft.api.fuel.FluidFuelManager.addFuel(fuel, 0);
        } catch (Exception e) {
            GroovyLog.msg("Error removing Railcraft Fluid Fuel")
                    .add("{}", e.getMessage())
                    .error()
                    .post();
        }
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        // Note: This is a limitation - we can't truly remove all without reflection
        // This method is provided for API compatibility but may not work as expected
        GroovyLog.msg("Warning: Railcraft FluidFuels.removeAll() may not work as expected")
                .add("Railcraft doesn't provide a way to clear all fluid fuels")
                .warn()
                .post();
    }

    public static class FuelEntry {
        public final FluidStack fluid;
        public final int heatValue;

        public FuelEntry(FluidStack fluid, int heatValue) {
            this.fluid = fluid;
            this.heatValue = heatValue;
        }
    }
}
