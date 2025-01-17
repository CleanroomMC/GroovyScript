package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
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

@RegistryDescription
public class PressurizedReactionChamber extends VirtualizedMekanismRegistry<PressurizedRecipe> {

    public PressurizedReactionChamber() {
        super(RecipeHandler.Recipe.PRESSURIZED_REACTION_CHAMBER, Alias.generateOfClass(PressurizedReactionChamber.class).and("PRC", "prc"));
    }

    @RecipeBuilderDescription(example = @Example(".fluidInput(fluid('water')).gasInput(gas('water')).input(item('minecraft:clay_ball')).gasOutput(gas('ethene'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public PressurizedRecipe add(IIngredient inputSolid, FluidStack inputFluid, GasStack inputGas, ItemStack outputSolid, GasStack outputGas, double energy, int duration) {
        return recipeBuilder().duration(duration).energy(energy).gasOutput(outputGas).gasInput(inputGas).fluidInput(inputFluid).output(outputSolid).input(inputSolid).register();
    }

    @MethodDescription(example = @Example("ore('logWood'), fluid('water'), gas('oxygen')"))
    public boolean removeByInput(IIngredient inputSolid, FluidStack inputFluid, GasStack inputGas) {
        if (GroovyLog.msg("Error removing Mekanism Pressurized Reaction Chamber recipe")
                .error()
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

    @Property(property = "input", comp = @Comp(gte = 0, lte = 1))
    @Property(property = "output", comp = @Comp(gte = 0, lte = 1))
    @Property(property = "fluidInput", comp = @Comp(eq = 1))
    @Property(property = "gasInput", comp = @Comp(eq = 1))
    @Property(property = "gasOutput", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends GasRecipeBuilder<PressurizedRecipe> {

        @Property(comp = @Comp(gt = 0))
        private int duration;
        @Property(comp = @Comp(gt = 0))
        private double energy;

        @RecipeBuilderMethodDescription
        public RecipeBuilder duration(int duration) {
            this.duration = duration;
            return this;
        }

        @RecipeBuilderMethodDescription
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
        @RecipeBuilderRegistrationMethod
        public @Nullable PressurizedRecipe register() {
            if (!validate()) return null;
            PressurizedOutput pressurizedOutput = new PressurizedOutput(output.getOrEmpty(0), gasOutput.get(0));
            PressurizedRecipe recipe = null;
            if (input.isEmpty()) {
                recipe = new PressurizedRecipe(new PressurizedInput(ItemStack.EMPTY, fluidInput.get(0), gasInput.get(0)), pressurizedOutput, energy, duration);
                ModSupport.MEKANISM.get().pressurizedReactionChamber.add(recipe);
            } else {
                for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                    PressurizedRecipe r = new PressurizedRecipe(new PressurizedInput(itemStack, fluidInput.get(0), gasInput.get(0)), pressurizedOutput, energy, duration);
                    if (recipe == null) recipe = r;
                    ModSupport.MEKANISM.get().pressurizedReactionChamber.add(r);
                }
            }
            return recipe;
        }
    }
}
