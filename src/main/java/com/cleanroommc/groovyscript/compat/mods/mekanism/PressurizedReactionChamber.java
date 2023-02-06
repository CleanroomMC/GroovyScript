package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.api.GroovyLog;
import mekanism.api.gas.GasStack;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.PressurizedInput;
import mekanism.common.recipe.machines.PressurizedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PressurizedReactionChamber extends VirtualizedMekanismRegistry<PressurizedRecipe> {

    public PressurizedReactionChamber() {
        super(RecipeHandler.Recipe.PRESSURIZED_REACTION_CHAMBER, "PRC");
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
            removeError("could not find recipe for %s, %s and %s", inputSolid, inputFluid, inputGas);
        }
        return found;
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<PressurizedRecipe> {

        private final List<GasStack> gasInput = new ArrayList<>();
        private final List<GasStack> gasOutput = new ArrayList<>();
        private int duration;
        private double energy;

        public RecipeBuilder gasInput(GasStack gas) {
            this.gasInput.add(gas);
            return this;
        }

        public RecipeBuilder gasInput(Collection<GasStack> gases) {
            if (gases != null && !gases.isEmpty()) {
                for (GasStack gas : gasInput) {
                    gasInput(gas);
                }
            }
            return this;
        }

        public RecipeBuilder gasInput(GasStack... gases) {
            if (gases != null && gases.length > 0) {
                for (GasStack gas : gasInput) {
                    gasInput(gas);
                }
            }
            return this;
        }

        public RecipeBuilder gasOutput(GasStack gas) {
            this.gasOutput.add(gas);
            return this;
        }

        public RecipeBuilder gasOutput(Collection<GasStack> gases) {
            if (gases != null && !gases.isEmpty()) {
                for (GasStack gas : gasOutput) {
                    gasOutput(gas);
                }
            }
            return this;
        }

        public RecipeBuilder gasOutput(GasStack... gases) {
            if (gases != null && gases.length > 0) {
                for (GasStack gas : gasOutput) {
                    gasOutput(gas);
                }
            }
            return this;
        }

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
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg, 1, 1, 0, 0);
            this.gasInput.removeIf(Mekanism::isEmpty);
            this.gasOutput.removeIf(Mekanism::isEmpty);
            msg.add(this.gasInput.size() != 1, () -> getRequiredString(1, 1, " gas input") + ", but found " + this.gasInput.size());
            msg.add(this.gasOutput.size() != 1, () -> getRequiredString(1, 1, " gas output") + ", but found " + this.gasOutput.size());
            if (duration <= 0) duration = 100;
            if (energy <= 0) energy = 8000;
        }

        @Override
        public @Nullable PressurizedRecipe register() {
            if (!validate()) return null;
            return ModSupport.MEKANISM.get().pressurizedReactionChamber.add(input.get(0), fluidInput.get(0), gasInput.get(0), output.get(0), gasOutput.get(0), energy, duration);
        }
    }
}
