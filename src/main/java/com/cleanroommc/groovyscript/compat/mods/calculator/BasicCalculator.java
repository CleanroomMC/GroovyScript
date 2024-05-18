package com.cleanroommc.groovyscript.compat.mods.calculator;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import sonar.calculator.mod.common.recipes.CalculatorRecipe;
import sonar.calculator.mod.common.recipes.CalculatorRecipes;
import sonar.core.recipes.ISonarRecipeObject;

import java.util.ArrayList;

@RegistryDescription
public class BasicCalculator extends VirtualizedRegistry<CalculatorRecipe> {

    public BasicCalculator() {
        super(Alias.generateOfClass(BasicCalculator.class).andGenerate("Calculator"));
    }

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay'), item('minecraft:clay')).output(item('minecraft:gold_ingot'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(CalculatorRecipes.instance().getRecipes()::remove);
        restoreFromBackup().forEach(CalculatorRecipes.instance().getRecipes()::add);
    }

    public void add(CalculatorRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        CalculatorRecipes.instance().getRecipes().add(recipe);
    }

    public boolean remove(CalculatorRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        CalculatorRecipes.instance().getRecipes().remove(recipe);
        return true;
    }

    @MethodDescription(example = @Example("item('minecraft:cobblestone')"))
    public boolean removeByInput(IIngredient input) {
        return CalculatorRecipes.instance().getRecipes().removeIf(r -> {
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
        return CalculatorRecipes.instance().getRecipes().removeIf(r -> {
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

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        CalculatorRecipes.instance().getRecipes().forEach(this::addBackup);
        CalculatorRecipes.instance().getRecipes().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<CalculatorRecipe> streamRecipes() {
        return new SimpleObjectStream<>(CalculatorRecipes.instance().getRecipes())
                .setRemover(this::remove);
    }

    @Property(property = "input", valid = @Comp("2"))
    @Property(property = "output", valid = @Comp("1"))
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
