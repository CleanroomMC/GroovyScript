package com.cleanroommc.groovyscript.compat.mods.calculator;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import sonar.calculator.mod.common.recipes.CalculatorRecipe;
import sonar.calculator.mod.common.recipes.PrecisionChamberRecipes;
import sonar.core.recipes.ISonarRecipeObject;

import java.util.ArrayList;
import java.util.Collection;

@RegistryDescription
public class PrecisionChamber extends StandardListRegistry<CalculatorRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond'), item('calculator:circuitdamaged:4'))"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:diamond'), item('minecraft:diamond'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<CalculatorRecipe> getRecipes() {
        return PrecisionChamberRecipes.instance().getRecipes();
    }

    @MethodDescription(example = @Example("item('minecraft:clay')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> {
            for (ISonarRecipeObject recipeInput : r.recipeInputs) {
                for (ItemStack itemStack : recipeInput.getJEIValue()) {
                    if (input.test(itemStack)) {
                        addBackup(r);
                        return true;
                    }
                }
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('calculator:soil')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> {
            for (ISonarRecipeObject recipeOutput : r.recipeOutputs) {
                for (ItemStack itemStack : recipeOutput.getJEIValue()) {
                    if (output.test(itemStack)) {
                        addBackup(r);
                        return true;
                    }
                }
            }
            return false;
        });
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("2"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<CalculatorRecipe> {

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Calculator Precision Chamber Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 2, 2);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CalculatorRecipe register() {
            if (!validate()) return null;

            CalculatorRecipe recipe = PrecisionChamberRecipes.instance()
                    .buildDefaultRecipe(Calculator.toSonarRecipeObjectList(input), output, new ArrayList<>(), false);

            ModSupport.CALCULATOR.get().precisionChamber.add(recipe);
            return recipe;
        }
    }
}
