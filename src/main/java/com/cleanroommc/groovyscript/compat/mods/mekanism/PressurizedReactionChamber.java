package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.GasRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import mekanism.api.gas.GasStack;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.PressurizedInput;
import mekanism.common.recipe.machines.PressurizedRecipe;
import mekanism.common.recipe.outputs.PressurizedOutput;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class PressurizedReactionChamber extends VirtualizedMekanismRegistry<PressurizedRecipe> {

    public PressurizedReactionChamber() {
        super(RecipeHandler.Recipe.PRESSURIZED_REACTION_CHAMBER, Alias.generateOfClass(PressurizedReactionChamber.class).and("PRC", "prc"));
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public PressurizedRecipe add(IIngredient inputSolid, FluidStack inputFluid, GasStack inputGas, ItemStack outputSolid, GasStack outputGas, double energy, int duration) {
        PressurizedRecipe r = null;
        for (ItemStack item : inputSolid.getMatchingStacks()) {
            PressurizedRecipe recipe = new PressurizedRecipe(item, inputFluid.copy(), inputGas.copy(), outputSolid.copy(), outputGas.copy(), energy, duration);
            if (r == null) r = recipe;
            recipeRegistry.put(recipe);
            addScripted(recipe);
        }
        return r;
    }

    public boolean removeByInput(IIngredient inputSolid, FluidStack inputFluid, GasStack inputGas) {
        if (GroovyLog.msg("Error removing Mekanism Pressurized Reaction Chamber recipe").error()
                .add(IngredientHelper.isEmpty(inputSolid), () -> "item input must not be empty")
                .add(IngredientHelper.isEmpty(inputFluid), () -> "fluid input must not be empty")
                .add(Mekanism.isEmpty(inputGas), () -> "input gas must not be empty")
                .error()
                .postIfNotEmpty()) {
            return false;
        }
        boolean found = false;
        for (ItemStack itemStack : inputSolid.getMatchingStacks()) {
            PressurizedRecipe recipe = recipeRegistry.get().remove(new PressurizedInput(itemStack, inputFluid, inputGas));
            if (recipe != null) {
                addBackup(recipe);
                found = true;
            }
        }
        if (!found) {
            removeError("could not find recipe for {}, {}, and {}", inputSolid, inputFluid, inputGas);
        }
        return found;
    }

    public static class RecipeBuilder extends GasRecipeBuilder<PressurizedRecipe> {

        private int duration;
        private double energy;

        public RecipeBuilder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public RecipeBuilder energy(double energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Pressurized Reaction Chamber recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 1, 0, 1);
            validateFluids(msg, 1, 1, 0, 0);
            validateGases(msg, 1, 1, 1, 1);
            if (duration <= 0) duration = 100;
            if (energy <= 0) energy = 8000;
        }

        @Override
        public @Nullable PressurizedRecipe register() {
            if (!validate()) return null;
            PressurizedOutput pressurizedOutput = new PressurizedOutput(output.getOrEmpty(0), gasOutput.get(0));
            PressurizedRecipe recipe = null;
            if (input.isEmpty()) {
                recipe = new PressurizedRecipe(new PressurizedInput(ItemStack.EMPTY, fluidInput.get(0), gasInput.get(0)), pressurizedOutput, energy, duration);
                ModSupport.MEKANISM.get().pressurizedReactionChamber.add(recipe);
            } else {
                for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                    PressurizedRecipe r = new PressurizedRecipe(new PressurizedInput(itemStack.copy(), fluidInput.get(0), gasInput.get(0)), pressurizedOutput, energy, duration);
                    if (recipe == null) recipe = r;
                    ModSupport.MEKANISM.get().pressurizedReactionChamber.add(r);
                }
            }
            return recipe;
        }
    }
}
