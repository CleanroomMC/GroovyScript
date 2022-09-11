package com.cleanroommc.groovyscript.compat.mods.ic2.exp;

import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import ic2.api.recipe.IFluidHeatManager;
import ic2.api.recipe.Recipes;
import ic2.core.block.heatgenerator.tileentity.TileEntityFluidHeatGenerator;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

public class FluidHeater extends VirtualizedRegistry<Pair<String, IFluidHeatManager.BurnProperty>> {

    public FluidHeater() {
        super("FluidHeater", "Firebox", "fluidheater", "firebox");
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> Recipes.fluidHeatGenerator.getBurnProperties().remove(pair.getKey()));
        restoreFromBackup().forEach(pair -> Recipes.fluidHeatGenerator.getBurnProperties().put(pair.getKey(), pair.getValue()));
    }

    public Pair<String, IFluidHeatManager.BurnProperty> add(FluidStack input, int heat) {
        return add(input.getFluid().getName(), new IFluidHeatManager.BurnProperty(input.amount, heat));
    }

    public Pair<String, IFluidHeatManager.BurnProperty> add(String name, IFluidHeatManager.BurnProperty recipe) {
        Pair<String, IFluidHeatManager.BurnProperty> pair = Pair.of(name, recipe);
        TileEntityFluidHeatGenerator.addFuel(name, recipe.amount, recipe.heat);
        addScripted(pair);
        return pair;
    }

    public boolean remove(FluidStack input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Liquid Fuel Firebox recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return false;
        }
        return remove(input.getFluid().getName());
    }

    public void removeAll() {
        for (String fluid : Recipes.fluidHeatGenerator.getBurnProperties().keySet()) {
            remove(fluid);
        }
    }

    public boolean remove(String name) {
        IFluidHeatManager.BurnProperty property = Recipes.fluidHeatGenerator.getBurnProperties().remove(name);
        if (property != null) {
            addBackup(Pair.of(name, property));
            return true;
        }

        return false;
    }
}
