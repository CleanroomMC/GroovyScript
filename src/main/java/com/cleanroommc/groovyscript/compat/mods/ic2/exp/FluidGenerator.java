package com.cleanroommc.groovyscript.compat.mods.ic2.exp;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.core.mixin.ic2.SemiFluidFuelManagerAccessor;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import ic2.api.recipe.ISemiFluidFuelManager;
import ic2.api.recipe.Recipes;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public class FluidGenerator extends VirtualizedRegistry<Pair<String, ISemiFluidFuelManager.FuelProperty>> {

    public FluidGenerator() {
        super(Alias.generateOfClassAnd(FluidGenerator.class, "SemiFluidGenerator"));
    }

    @Override
    public void onReload() {
        Map<String, ISemiFluidFuelManager.FuelProperty> fluidMap = ((SemiFluidFuelManagerAccessor) Recipes.semiFluidGenerator).getFuelProperties();
        removeScripted().forEach(pair -> fluidMap.remove(pair.getKey()));
        restoreFromBackup().forEach(pair -> fluidMap.put(pair.getKey(), pair.getValue()));
    }

    public Pair<String, ISemiFluidFuelManager.FuelProperty> add(FluidStack input, long energyPerMb, long energyPerTick) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Semi Fluid Generator recipe")
                .add(IngredientHelper.isEmpty(input), () -> "input must not be empty")
                .add(energyPerMb <= 0, () -> "energy per mb must be higher than zero")
                .add(energyPerTick <= 0, () -> "energy per tick must be higher than zero")
                .error()
                .postIfNotEmpty()) {
            return null;
        }
        Pair<String, ISemiFluidFuelManager.FuelProperty> pair = Pair.of(input.getFluid().getName(), new ISemiFluidFuelManager.FuelProperty(energyPerMb, energyPerTick));
        Recipes.semiFluidGenerator.addFluid(input.getFluid().getName(), energyPerMb, energyPerTick);
        addScripted(pair);
        return pair;
    }

    public Pair<String, ISemiFluidFuelManager.FuelProperty> add(String name, ISemiFluidFuelManager.FuelProperty recipe) {
        Map<String, ISemiFluidFuelManager.FuelProperty> fluidMap = ((SemiFluidFuelManagerAccessor) Recipes.semiFluidGenerator).getFuelProperties();
        Pair<String, ISemiFluidFuelManager.FuelProperty> pair = Pair.of(name, recipe);
        fluidMap.put(name, recipe);
        addScripted(pair);
        return pair;
    }

    public SimpleObjectStream<Map.Entry<String, ISemiFluidFuelManager.FuelProperty>> streamRecipes() {
        return new SimpleObjectStream<>(Recipes.semiFluidGenerator.getFuelProperties().entrySet()).setRemover(r -> remove(r.getKey()));
    }

    public boolean remove(FluidStack input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Semi Fluid Generator recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return false;
        }
        return remove(input.getFluid().getName());
    }

    public void removeAll() {
        Map<String, ISemiFluidFuelManager.FuelProperty> fluidMap = ((SemiFluidFuelManagerAccessor) Recipes.semiFluidGenerator).getFuelProperties();
        for (String fluid : fluidMap.keySet()) {
            addBackup(Pair.of(fluid, fluidMap.get(fluid)));
            fluidMap.remove(fluid);
        }
    }

    public boolean remove(String name) {
        Map<String, ISemiFluidFuelManager.FuelProperty> fluidMap = ((SemiFluidFuelManagerAccessor) Recipes.semiFluidGenerator).getFuelProperties();
        ISemiFluidFuelManager.FuelProperty property = fluidMap.remove(name);
        if (property != null) {
            addBackup(Pair.of(name, property));
            return true;
        }

        return false;
    }
}
