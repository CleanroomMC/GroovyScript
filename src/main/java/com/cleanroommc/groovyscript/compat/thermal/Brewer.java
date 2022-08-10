package com.cleanroommc.groovyscript.compat.thermal;

import cofh.thermalexpansion.util.managers.machine.BrewerManager;
import com.cleanroommc.groovyscript.compat.EnergyRecipeBuilder;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
import com.cleanroommc.groovyscript.sandbox.GroovyLog;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class Brewer {

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void removeByInput(ItemStack itemStack, FluidStack input) {
        if (new GroovyLog.Msg("Error removing Thermal Expansion Brewer recipe").error()
                .add(IngredientHelper.isEmpty(itemStack), () -> "item input must not be empty")
                .add(IngredientHelper.isEmpty(input), () -> "fluid input must not be empty")
                .logIfNotEmpty()) {
            BrewerManager.removeRecipe(itemStack, input);
        }
    }

    public static class RecipeBuilder extends EnergyRecipeBuilder<BrewerManager.BrewerRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Thermal Expansion Brewer recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg, 1, 1, 1, 1);
            if (energy <= 0) energy = 4800;
        }

        @Override
        public @Nullable BrewerManager.BrewerRecipe buildAndRegister() {
            if (!validate()) return null;
            BrewerManager.BrewerRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                BrewerManager.BrewerRecipe recipe1 = BrewerManager.addRecipe(energy, itemStack, fluidInput.get(0), fluidOutput.get(0));
                if (recipe == null) {
                    recipe = recipe1;
                }
            }
            return recipe;
        }
    }
}
