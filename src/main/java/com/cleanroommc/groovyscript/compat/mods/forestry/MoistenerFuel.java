package com.cleanroommc.groovyscript.compat.mods.forestry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import forestry.api.fuels.FuelManager;
import net.minecraft.item.ItemStack;

public class MoistenerFuel extends ForestryRegistry<forestry.api.fuels.MoistenerFuel> {

    @Override
    @GroovyBlacklist
    public void onReload() {
        if (!isEnabled()) return;
        removeScripted().forEach(fuel -> FuelManager.moistenerResource.remove(fuel.getItem(), fuel));
        restoreFromBackup().forEach(fuel -> FuelManager.moistenerResource.put(fuel.getItem(), fuel));
    }

    public forestry.api.fuels.MoistenerFuel add(ItemStack output, IIngredient input, int value, int stage) {
        forestry.api.fuels.MoistenerFuel fuel = new forestry.api.fuels.MoistenerFuel(output, input.getMatchingStacks()[0], stage, value);
        add(fuel);
        return fuel;
    }

    public void add(forestry.api.fuels.MoistenerFuel fuel) {
        if (fuel == null) return;
        addScripted(fuel);
        FuelManager.moistenerResource.put(fuel.getItem(), fuel);
    }

    public boolean remove(forestry.api.fuels.MoistenerFuel fuel) {
        if (fuel == null) return false;
        addBackup(fuel);
        FuelManager.moistenerResource.remove(fuel.getItem(), fuel);
        return true;
    }

    public boolean removeByInput(IIngredient input) {
        if (FuelManager.moistenerResource.values().removeIf(fuel -> {
            boolean found = input.test(fuel.getItem());
            if (found) addBackup(fuel);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Moistener fuel")
                .add("Could not find fuel with input {}", input)
                .error()
                .post();
        return false;
    }

    public boolean removeByOutput(ItemStack output) {
        if (FuelManager.moistenerResource.values().removeIf(fuel -> {
            boolean found = fuel.getProduct().isItemEqual(output);
            if (found) addBackup(fuel);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Forestry Moistener fuel")
                .add("Could not find fuel with output {}", output)
                .error()
                .post();
        return false;
    }

    public void removeAll() {
        FuelManager.moistenerResource.values().forEach(this::addBackup);
        FuelManager.moistenerResource.clear();
    }

    public SimpleObjectStream<forestry.api.fuels.MoistenerFuel> streamFuels() {
        return new SimpleObjectStream<>(FuelManager.moistenerResource.values()).setRemover(this::remove);
    }
}
