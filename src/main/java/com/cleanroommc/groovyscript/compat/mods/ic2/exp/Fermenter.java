package com.cleanroommc.groovyscript.compat.mods.ic2.exp;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.core.mixin.ic2.FermenterRecipeManagerAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import ic2.api.recipe.IFermenterRecipeManager;
import ic2.api.recipe.Recipes;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public class Fermenter extends VirtualizedRegistry<Pair<String, IFermenterRecipeManager.FermentationProperty>> {

    @Override
    public void onReload() {
        Map<String, IFermenterRecipeManager.FermentationProperty> fluidMap = ((FermenterRecipeManagerAccessor) Recipes.fermenter).getFluidMap();
        removeScripted().forEach(pair -> fluidMap.remove(pair.getKey()));
        restoreFromBackup().forEach(pair -> fluidMap.put(pair.getKey(), pair.getValue()));
    }

    public Pair<String, IFermenterRecipeManager.FermentationProperty> add(FluidStack input, int heat, FluidStack output) {
        if (GroovyLog.msg("Error adding Industrialcraft 2 Fermenter recipe")
                .add(IngredientHelper.isEmpty(input), () -> "input must not be empty")
                .add(heat <= 0, () -> "heat must be higher than zero")
                .add(IngredientHelper.isEmpty(output), () -> "output must not be empty")
                .error()
                .postIfNotEmpty()) {
            return null;
        }
        Pair<String, IFermenterRecipeManager.FermentationProperty> pair = Pair.of(input.getFluid().getName(), new IFermenterRecipeManager.FermentationProperty(input.amount, heat, output.getFluid().getName(), output.amount));
        Recipes.fermenter.addRecipe(input.getFluid().getName(), input.amount, heat, output.getFluid().getName(), output.amount);
        addScripted(pair);
        return pair;
    }

    public void add(String input, IFermenterRecipeManager.FermentationProperty recipe) {
        Map<String, IFermenterRecipeManager.FermentationProperty> fluidMap = ((FermenterRecipeManagerAccessor) Recipes.fermenter).getFluidMap();
        fluidMap.put(input, recipe);
        addScripted(Pair.of(input, recipe));
    }

    public SimpleObjectStream<Map.Entry<String, IFermenterRecipeManager.FermentationProperty>> streamRecipes() {
        return new SimpleObjectStream<>(Recipes.fermenter.getRecipeMap().entrySet()).setRemover(r -> this.remove(r.getKey()));
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
        Map<String, IFermenterRecipeManager.FermentationProperty> fluidMap = ((FermenterRecipeManagerAccessor) Recipes.fermenter).getFluidMap();
        for (Map.Entry<String, IFermenterRecipeManager.FermentationProperty> entry : fluidMap.entrySet()) {
            FluidStack out = entry.getValue().getOutput();
            if (out.isFluidEqual(output) && out.amount == output.amount) {
                remove(entry.getKey());
            }
        }
    }

    public void removeAll() {
        Map<String, IFermenterRecipeManager.FermentationProperty> fluidMap = ((FermenterRecipeManagerAccessor) Recipes.fermenter).getFluidMap();
        for (String name : fluidMap.keySet()) {
            remove(name);
        }
    }

    public boolean remove(String input) {
        if (StringUtils.isEmpty(input)) {
            GroovyLog.msg("Error removing Industrialcraft 2 Fermenter recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
            return false;
        }
        Map<String, IFermenterRecipeManager.FermentationProperty> fluidMap = ((FermenterRecipeManagerAccessor) Recipes.fermenter).getFluidMap();
        IFermenterRecipeManager.FermentationProperty property = fluidMap.remove(input);
        if (property != null) {
            addBackup(Pair.of(input, property));
            return true;
        }
        GroovyLog.msg("Error removing Industrialcraft 2 Fermenter recipe")
                .add("no recipes found for {}", input)
                .error()
                .post();
        return false;
    }
}
