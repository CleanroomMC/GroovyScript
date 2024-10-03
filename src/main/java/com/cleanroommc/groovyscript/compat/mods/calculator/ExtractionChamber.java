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
import sonar.calculator.mod.common.recipes.ExtractionChamberRecipes;
import sonar.core.recipes.ISonarRecipeObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RegistryDescription
public class ExtractionChamber extends StandardListRegistry<CalculatorRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond'))"),
            @Example(".input(item('minecraft:gold_ingot')).output(item('minecraft:clay')).isDamaged()")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<CalculatorRecipe> getRecipes() {
        return ExtractionChamberRecipes.instance().getRecipes();
    }

    @MethodDescription(example = @Example("item('minecraft:dirt')"))
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

    @MethodDescription(example = @Example("item('calculator:smallstone')"))
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

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<CalculatorRecipe> {

        @Property
        private boolean isDamaged;

        @RecipeBuilderMethodDescription
        public RecipeBuilder isDamaged() {
            this.isDamaged = !isDamaged;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder isDamaged(boolean isDamaged) {
            this.isDamaged = isDamaged;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Calculator Extraction Chamber Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CalculatorRecipe register() {
            if (!validate()) return null;

            List<Object> specialOutput = new ArrayList<>();
            specialOutput.add(output.get(0));
            specialOutput.add(
                    new ExtractionChamberRecipes.ExtractionChamberOutput(
                            new ItemStack(
                                    isDamaged ? sonar.calculator.mod.Calculator.circuitDamaged : sonar.calculator.mod.Calculator.circuitDirty,
                                    1,
                                    32767)));

            CalculatorRecipe recipe = ExtractionChamberRecipes.instance()
                    .buildDefaultRecipe(Calculator.toSonarRecipeObjectList(input), specialOutput, new ArrayList<>(), false);

            ModSupport.CALCULATOR.get().extractionChamber.add(recipe);
            return recipe;
        }

    }

}
