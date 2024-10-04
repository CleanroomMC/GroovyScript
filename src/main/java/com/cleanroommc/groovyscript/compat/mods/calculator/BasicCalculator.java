package com.cleanroommc.groovyscript.compat.mods.calculator;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import sonar.calculator.mod.common.recipes.CalculatorRecipe;
import sonar.calculator.mod.common.recipes.CalculatorRecipes;
import sonar.core.recipes.ISonarRecipeObject;

import java.util.ArrayList;
import java.util.Collection;

@RegistryDescription
public class BasicCalculator extends StandardListRegistry<CalculatorRecipe> {

    public BasicCalculator() {
        super(Alias.generateOfClass(BasicCalculator.class).andGenerate("Calculator"));
    }

    @Override
    public Collection<CalculatorRecipe> getRecipes() {
        return CalculatorRecipes.instance().getRecipes();
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay'), item('minecraft:clay')).output(item('minecraft:gold_ingot'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("item('minecraft:cobblestone')"))
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

    @MethodDescription(example = @Example("item('calculator:reinforcedironingot')"))
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

    @Property(property = "input", comp = @Comp(eq = 2))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<CalculatorRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Calculator Basic Calculator Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 2, 2, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CalculatorRecipe register() {
            if (!validate()) return null;

            CalculatorRecipe recipe = CalculatorRecipes.instance()
                    .buildDefaultRecipe(Calculator.toSonarRecipeObjectList(input), output, new ArrayList<>(), false);

            ModSupport.CALCULATOR.get().basicCalculator.add(recipe);
            return recipe;
        }
    }
}
