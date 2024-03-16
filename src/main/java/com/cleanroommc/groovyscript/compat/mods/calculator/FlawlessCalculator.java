package com.cleanroommc.groovyscript.compat.mods.calculator;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import sonar.calculator.mod.common.recipes.CalculatorRecipe;
import sonar.calculator.mod.common.recipes.FlawlessCalculatorRecipes;
import sonar.core.recipes.ISonarRecipeObject;

import java.util.ArrayList;

@RegistryDescription
public class FlawlessCalculator extends VirtualizedRegistry<CalculatorRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay'), item('minecraft:clay')).output(item('minecraft:gold_ingot') * 16)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(FlawlessCalculatorRecipes.instance().getRecipes()::remove);
        restoreFromBackup().forEach(FlawlessCalculatorRecipes.instance().getRecipes()::add);
    }

    public void add(CalculatorRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        FlawlessCalculatorRecipes.instance().getRecipes().add(recipe);
    }

    public boolean remove(CalculatorRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        FlawlessCalculatorRecipes.instance().getRecipes().remove(recipe);
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('minecraft:obsidian')"))
    public boolean removeByInput(IIngredient input) {
        return FlawlessCalculatorRecipes.instance().getRecipes().removeIf(r -> {
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

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('minecraft:ender_pearl')"))
    public boolean removeByOutput(IIngredient output) {
        return FlawlessCalculatorRecipes.instance().getRecipes().removeIf(r -> {
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

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        FlawlessCalculatorRecipes.instance().getRecipes().forEach(this::addBackup);
        FlawlessCalculatorRecipes.instance().getRecipes().clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<CalculatorRecipe> streamRecipes() {
        return new SimpleObjectStream<>(FlawlessCalculatorRecipes.instance().getRecipes())
                .setRemover(this::remove);
    }

    @Property(property = "input", valid = @Comp("4"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<CalculatorRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Calculator Flawless Calculator Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 4, 4, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CalculatorRecipe register() {
            if (!validate()) return null;

            CalculatorRecipe recipe = FlawlessCalculatorRecipes.instance()
                    .buildDefaultRecipe(Calculator.toSonarRecipeObjectList(input), output, new ArrayList<>(), false);

            ModSupport.CALCULATOR.get().flawlessCalculator.add(recipe);
            return recipe;
        }
    }
}
