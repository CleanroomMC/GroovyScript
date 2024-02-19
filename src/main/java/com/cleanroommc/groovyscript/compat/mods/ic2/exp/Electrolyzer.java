package com.cleanroommc.groovyscript.compat.mods.ic2.exp;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.core.mixin.ic2.ElectrolyzerRecipeManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import ic2.api.recipe.IElectrolyzerRecipeManager;
import ic2.api.recipe.Recipes;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Electrolyzer extends VirtualizedRegistry<Pair<String, IElectrolyzerRecipeManager.ElectrolyzerRecipe>> {

    @Override
    public void onReload() {
        Map<String, IElectrolyzerRecipeManager.ElectrolyzerRecipe> fluidMap = ((ElectrolyzerRecipeManagerAccessor) Recipes.electrolyzer).getFluidMap();
        removeScripted().forEach(pair -> fluidMap.remove(pair.getKey()));
        restoreFromBackup().forEach(pair -> add(pair.getKey(), pair.getValue(), false));
    }

    public Pair<String, IElectrolyzerRecipeManager.ElectrolyzerRecipe> add(String name, IElectrolyzerRecipeManager.ElectrolyzerRecipe recipe) {
        return add(name, recipe, true);
    }

    public SimpleObjectStream<Map.Entry<String, IElectrolyzerRecipeManager.ElectrolyzerRecipe>> streamRecipes() {
        return new SimpleObjectStream<>(Recipes.electrolyzer.getRecipeMap().entrySet()).setRemover(r -> remove(r.getKey(), r.getValue()));
    }

    public boolean remove(String name, IElectrolyzerRecipeManager.ElectrolyzerRecipe recipe) {
        Map<String, IElectrolyzerRecipeManager.ElectrolyzerRecipe> fluidMap = ((ElectrolyzerRecipeManagerAccessor) Recipes.electrolyzer).getFluidMap();
        if (fluidMap.remove(name, recipe)) {
            addBackup(Pair.of(name, recipe));
            return true;
        }

        return false;
    }

    public Pair<String, IElectrolyzerRecipeManager.ElectrolyzerRecipe> add(FluidStack input, int euATick, int ticksNeeded, FluidStack... outputs) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Electrolyzer recipe")
                .add(IngredientHelper.isEmpty(input), () -> "input must not be empty")
                .add(euATick <= 0, () -> "energy per tick must be higher than zero")
                .add(ticksNeeded <= 0, () -> "recipe time must be higher than zero")
                .add(outputs == null || outputs.length <= 0, () -> "outputs must not be null")
                .error()
                .postIfNotEmpty()) {
            return null;
        }
        List<IElectrolyzerRecipeManager.ElectrolyzerOutput> list = new ArrayList<>();
        for (int i = 0; i < outputs.length; i++) {
            FluidStack fs = outputs[i];
            if (fs != null)
                list.add(new IElectrolyzerRecipeManager.ElectrolyzerOutput(fs.getFluid().getName(), fs.amount, EnumFacing.values()[i]));
        }

        return add(input.getFluid().getName(), new IElectrolyzerRecipeManager.ElectrolyzerRecipe(input.amount, euATick, ticksNeeded, list.toArray(new IElectrolyzerRecipeManager.ElectrolyzerOutput[list.size()])));
    }

    public void removeByOutput(FluidStack... outputs) {
        if (outputs == null || outputs.length < 1) {
            GroovyLog.msg("Error removing Industrialcraft 2 Electrolyzer recipe")
                    .add("outputs must not be empty")
                    .error()
                    .post();
            return;
        }
        Map<String, IElectrolyzerRecipeManager.ElectrolyzerRecipe> fluidMap = ((ElectrolyzerRecipeManagerAccessor) Recipes.electrolyzer).getFluidMap();
        for (Map.Entry<String, IElectrolyzerRecipeManager.ElectrolyzerRecipe> recipe : fluidMap.entrySet()) {
            int i;
            for (i = 0; i < recipe.getValue().outputs.length; i++) {
                FluidStack out = recipe.getValue().outputs[i].getOutput();
                if (!out.isFluidEqual(outputs[i]) || (outputs[i] != null && out.amount != outputs[i].amount)) break;
            }

            if (i == outputs.length) {
                remove(recipe.getKey(), recipe.getValue());
            }
        }
    }

    public void removeByInput(FluidStack input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Electrolyzer recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return;
        }
        Map<String, IElectrolyzerRecipeManager.ElectrolyzerRecipe> fluidMap = ((ElectrolyzerRecipeManagerAccessor) Recipes.electrolyzer).getFluidMap();
        Fluid in = input.getFluid();
        addBackup(Pair.of(in.getName(), fluidMap.get(in.getName())));
        fluidMap.remove(in.getName());
    }

    public void removeAll() {
        Map<String, IElectrolyzerRecipeManager.ElectrolyzerRecipe> fluidMap = ((ElectrolyzerRecipeManagerAccessor) Recipes.electrolyzer).getFluidMap();
        for (Map.Entry<String, IElectrolyzerRecipeManager.ElectrolyzerRecipe> recipe : fluidMap.entrySet()) {
            remove(recipe.getKey(), recipe.getValue());
        }
    }

    private Pair<String, IElectrolyzerRecipeManager.ElectrolyzerRecipe> add(String name, IElectrolyzerRecipeManager.ElectrolyzerRecipe recipe, boolean scripted) {
        Pair<String, IElectrolyzerRecipeManager.ElectrolyzerRecipe> pair = Pair.of(name, recipe);
        Recipes.electrolyzer.addRecipe(name, recipe.inputAmount, recipe.EUaTick, recipe.ticksNeeded, recipe.outputs);
        if (scripted) addScripted(pair);
        return pair;
    }
}
