package com.cleanroommc.groovyscript.compat.mods.railcraft;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import mods.railcraft.api.fuel.FluidFuelManager;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;



@RegistryDescription
public class FluidFuels extends VirtualizedRegistry<FluidFuels.FuelEntry> {

    private final Map<String, Integer> backupFuels = new HashMap<>();

    @Override
    public void onReload() {
        // Remove scripted (disable them by setting heat to 0)
        removeScripted().forEach(entry -> {
            try {
                // Disable the fuel by setting heat value to 0
                FluidFuelManager.addFuel(entry.fluid, 0);
            } catch (Exception e) {
                GroovyLog.msg("Error removing Railcraft Fluid Fuel")
                        .error()
                        .add("fluid: {}", entry.fluid.getFluid().getName())
                        .add("exception: {}", e.getMessage())
                        .post();
            }
        });
        
        // Restore from backup
        restoreFromBackup().forEach(entry -> {
            try {
                FluidFuelManager.addFuel(entry.fluid, entry.heatValue);
            } catch (Exception e) {
                GroovyLog.msg("Error restoring Railcraft Fluid Fuel")
                        .error()
                        .add("fluid: {}", entry.fluid.getFluid().getName())
                        .add("heat value: {}", entry.heatValue)
                        .add("exception: {}", e.getMessage())
                        .post();
            }
        });
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("fluid('lava'), 32000"))
    public void add(FluidStack fuel, int heatValue) {
        if (IngredientHelper.isEmpty(fuel)) {
            GroovyLog.msg("Error adding Railcraft Fluid Fuel")
                    .error()
                    .add("fuel must not be empty")
                    .post();
            return;
        }
        if (heatValue <= 0) {
            GroovyLog.msg("Error adding Railcraft Fluid Fuel")
                    .error()
                    .add("fluid: {}", fuel.getFluid().getName())
                    .add("heat value: {} (must be > 0)", heatValue)
                    .post();
            return;
        }

        // Store the original value for backup before adding
        int originalValue = FluidFuelManager.getFuelValue(fuel);
        if (originalValue > 0) {
            addBackup(new FuelEntry(fuel.copy(), originalValue));
        }
        
        addScripted(new FuelEntry(fuel.copy(), heatValue));

        try {
            FluidFuelManager.addFuel(fuel, heatValue);
        } catch (Exception e) {
            GroovyLog.msg("Error adding Railcraft Fluid Fuel")
                    .error()
                    .add("fluid: {}", fuel.getFluid().getName())
                    .add("heat value: {}", heatValue)
                    .add("exception: {}", e.getMessage())
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
                    .error()
                    .add("fuel must not be empty")
                    .post();
            return;
        }

        // Store current value for backup
        int currentValue = FluidFuelManager.getFuelValue(fuel);
        if (currentValue > 0) {
            addBackup(new FuelEntry(fuel.copy(), currentValue));
        }

        // Note: Railcraft's FluidFuelManager doesn't have a direct remove method
        // We can only add with 0 heat value to effectively disable it
        try {
            FluidFuelManager.addFuel(fuel, 0);
        } catch (Exception e) {
            GroovyLog.msg("Error removing Railcraft Fluid Fuel")
                    .error()
                    .add("fluid: {}", fuel.getFluid().getName())
                    .add("exception: {}", e.getMessage())
                    .post();
        }
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        // Note: This is a limitation - we can't truly remove all without reflection
        // This method is provided for API compatibility but may not work as expected
        GroovyLog.msg("Warning: Railcraft FluidFuels.removeAll() may not work as expected")
                .warn()
                .add("Railcraft doesn't provide a way to clear all fluid fuels")
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
