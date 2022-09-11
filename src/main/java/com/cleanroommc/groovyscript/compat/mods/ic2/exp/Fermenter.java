package com.cleanroommc.groovyscript.compat.mods.ic2.exp;

import com.cleanroommc.groovyscript.core.mixin.ic2.FermenterRecipeManagerAccessor;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import ic2.api.recipe.IFermenterRecipeManager;
import ic2.api.recipe.Recipes;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public class Fermenter extends VirtualizedRegistry<Pair<String, IFermenterRecipeManager.FermentationProperty>> {

    private static final Map<String, IFermenterRecipeManager.FermentationProperty> fluidMap = ((FermenterRecipeManagerAccessor) Recipes.fermenter).getFluidMap();

    public Fermenter() {
        super("Fermenter", "fermenter");
    }

    @Override
    public void onReload() {
        removeScripted().forEach(pair -> fluidMap.remove(pair.getKey()));
        restoreFromBackup().forEach(pair -> fluidMap.put(pair.getKey(), pair.getValue()));
    }

    public Pair<String, IFermenterRecipeManager.FermentationProperty> add(FluidStack input, int heat, FluidStack output) {
        Pair<String, IFermenterRecipeManager.FermentationProperty> pair = Pair.of(input.getFluid().getName(), new IFermenterRecipeManager.FermentationProperty(input.amount, heat, output.getFluid().getName(), output.amount));
        Recipes.fermenter.addRecipe(input.getFluid().getName(), input.amount, heat, output.getFluid().getName(), output.amount);
        addScripted(pair);
        return pair;
    }

    public void add(String input, IFermenterRecipeManager.FermentationProperty recipe) {
        fluidMap.put(input, recipe);
        addScripted(Pair.of(input, recipe));
    }

    public void removeByInput(FluidStack input) {
        if (IngredientHelper.isEmpty(input)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Fermenter recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return;
        }
        String name = input.getFluid().getName();
        remove(name);
    }

    public void removeByOutput(FluidStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Fermenter recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
            return;
        }
        for (Map.Entry<String, IFermenterRecipeManager.FermentationProperty> entry : fluidMap.entrySet()) {
            FluidStack out = entry.getValue().getOutput();
            if (out.isFluidEqual(output) && out.amount == output.amount) {
                remove(entry.getKey());
            }
        }
    }

    public void removeAll() {
        for (String name : fluidMap.keySet()) {
            remove(name);
        }
    }

    public boolean remove(String input) {
        IFermenterRecipeManager.FermentationProperty property = fluidMap.remove(input);
        if (property != null) {
            addBackup(Pair.of(input, property));
            return true;
        }

        return false;
    }
}
