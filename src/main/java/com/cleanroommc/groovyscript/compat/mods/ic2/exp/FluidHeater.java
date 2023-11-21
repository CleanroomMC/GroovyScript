package com.cleanroommc.groovyscript.compat.mods.ic2.exp;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import ic2.api.recipe.IFluidHeatManager;
import ic2.api.recipe.Recipes;
import ic2.core.block.heatgenerator.tileentity.TileEntityFluidHeatGenerator;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public class FluidHeater extends VirtualizedRegistry<Pair<String, IFluidHeatManager.BurnProperty>> {

    public FluidHeater() {
        super(Alias.generateOf("Firebox"));
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> Recipes.fluidHeatGenerator.getBurnProperties().remove(pair.getKey()));
        restoreFromBackup().forEach(pair -> Recipes.fluidHeatGenerator.getBurnProperties().put(pair.getKey(), pair.getValue()));
    }

    public Pair<String, IFluidHeatManager.BurnProperty> add(FluidStack input, int heat) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Fluid Heat Generator recipe")
                .add(IngredientHelper.isEmpty(input), () -> "input must not be empty")
                .add(heat <= 0, () -> "heat must be higher than zero")
                .error()
                .postIfNotEmpty()) {
            return null;
        }
        return add(input.getFluid().getName(), new IFluidHeatManager.BurnProperty(input.amount, heat));
    }

    public Pair<String, IFluidHeatManager.BurnProperty> add(String name, IFluidHeatManager.BurnProperty recipe) {
        Pair<String, IFluidHeatManager.BurnProperty> pair = Pair.of(name, recipe);
        TileEntityFluidHeatGenerator.addFuel(name, recipe.amount, recipe.heat);
        addScripted(pair);
        return pair;
    }

    public SimpleObjectStream<Map.Entry<String, IFluidHeatManager.BurnProperty>> streamRecipes() {
        return new SimpleObjectStream<>(Recipes.fluidHeatGenerator.getBurnProperties().entrySet()).setRemover(r -> remove(r.getKey()));
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
        if (StringUtils.isEmpty(name)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Liquid Fuel Firebox recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return false;
        }
        IFluidHeatManager.BurnProperty property = Recipes.fluidHeatGenerator.getBurnProperties().remove(name);
        if (property != null) {
            addBackup(Pair.of(name, property));
            return true;
        }

        return false;
    }
}
