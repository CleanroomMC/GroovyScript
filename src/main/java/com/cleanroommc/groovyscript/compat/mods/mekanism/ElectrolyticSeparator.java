package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.GasRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import mekanism.api.gas.GasStack;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.FluidInput;
import mekanism.common.recipe.machines.SeparatorRecipe;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class ElectrolyticSeparator extends VirtualizedMekanismRegistry<SeparatorRecipe> {

    public ElectrolyticSeparator() {
        super(RecipeHandler.Recipe.ELECTROLYTIC_SEPARATOR, Alias.generateOfClassAnd(ElectrolyticSeparator.class, "Separator"));
    }

    @RecipeBuilderDescription(example = @Example(".fluidInput(fluid('lava') * 10).gasOutput(gas('cleanGold') * 5, gas('cleanCopper') * 3).energy(3000)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "fluid('lava') * 10, gas('cleanGold') * 5, gas('cleanCopper') * 3, 3000", commented = true))
    public SeparatorRecipe add(FluidStack input, GasStack leftOutput, GasStack rightOutput, double energy) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Electrolytic Separator recipe").error();
        msg.add(IngredientHelper.isEmpty(input), () -> "input must not be empty");
        msg.add(Mekanism.isEmpty(leftOutput), () -> "left gas output must not be empty");
        msg.add(Mekanism.isEmpty(rightOutput), () -> "right gas output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        SeparatorRecipe recipe = new SeparatorRecipe(input.copy(), energy, leftOutput.copy(), rightOutput.copy());
        addScripted(recipe);
        recipeRegistry.put(recipe);
        return recipe;
    }

    @MethodDescription(example = @Example("fluid('water')"))
    public boolean removeByInput(FluidStack input) {
        GroovyLog.Msg msg = GroovyLog.msg("Error removing Mekanism Electrolytic Separator recipe").error();
        msg.add(IngredientHelper.isEmpty(input), () -> "input must not be empty");
        if (msg.postIfNotEmpty()) return false;

        SeparatorRecipe recipe = recipeRegistry.get().remove(new FluidInput(input));
        if (recipe != null) {
            addBackup(recipe);
            return true;
        }
        removeError("could not find recipe for {}", input);
        return false;
    }

    @Property(property = "fluidInput", valid = @Comp("1"))
    @Property(property = "gasOutput", valid = @Comp("2"))
    public static class RecipeBuilder extends GasRecipeBuilder<SeparatorRecipe> {

        @Property(valid = @Comp(type = Comp.Type.GT, value = "0"))
        private double energy;

        public RecipeBuilder energy(double energy) {
            this.energy = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Electrolytic Separator recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg, 1, 1, 0, 0);
            validateGases(msg, 0, 0, 2, 2);
            msg.add(energy <= 0, "energy must be a nonnegative integer, yet it was {}", energy);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable SeparatorRecipe register() {
            if (!validate()) return null;
            SeparatorRecipe recipe = new SeparatorRecipe(fluidInput.get(0), energy, gasOutput.get(0), gasOutput.get(1));
            ModSupport.MEKANISM.get().electrolyticSeparator.add(recipe);
            return recipe;
        }
    }
}
