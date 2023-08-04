package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.FluidInput;
import mekanism.common.recipe.machines.ThermalEvaporationRecipe;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ThermalEvaporationPlant extends VirtualizedMekanismRegistry<ThermalEvaporationRecipe> {

    public ThermalEvaporationPlant() {
        super(RecipeHandler.Recipe.THERMAL_EVAPORATION_PLANT, "TEP", "tep");
        aliases.addAll(Arrays.asList(VirtualizedMekanismRegistry.generateAliases("ThermalEvaporation")));
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public ThermalEvaporationRecipe add(FluidStack input, FluidStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Solar Neutron Activator recipe").error();
        msg.add(IngredientHelper.isEmpty(input), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(output), () -> "output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        ThermalEvaporationRecipe recipe = new ThermalEvaporationRecipe(input.copy(), output.copy());
        recipeRegistry.put(recipe);
        addScripted(recipe);
        return recipe;
    }

    public boolean removeByInput(FluidStack input) {
        GroovyLog.Msg msg = GroovyLog.msg("Error removing Mekanism Solar Neutron Activator recipe").error();
        msg.add(IngredientHelper.isEmpty(input), () -> "input must not be empty");
        if (msg.postIfNotEmpty()) return false;

        ThermalEvaporationRecipe recipe = recipeRegistry.get().remove(new FluidInput(input));
        if (recipe != null) {
            addBackup(recipe);
            return true;
        }
        removeError("could not find recipe for %", input);
        return false;
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<ThermalEvaporationRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Thermal Evaporation Plant recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg, 1, 1, 1, 1);
        }

        @Override
        public @Nullable ThermalEvaporationRecipe register() {
            if (!validate()) return null;
            ThermalEvaporationRecipe recipe = new ThermalEvaporationRecipe(fluidInput.get(0), fluidOutput.get(0));
            ModSupport.MEKANISM.get().thermalEvaporationPlant.add(recipe);
            return recipe;
        }
    }
}
