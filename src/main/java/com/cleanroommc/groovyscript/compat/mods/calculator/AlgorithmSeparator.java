package com.cleanroommc.groovyscript.compat.mods.calculator;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;
import sonar.calculator.mod.common.recipes.AlgorithmSeparatorRecipes;
import sonar.calculator.mod.common.recipes.CalculatorRecipe;
import sonar.core.recipes.ISonarRecipeObject;

import java.util.ArrayList;

@RegistryDescription
public class AlgorithmSeparator extends VirtualizedRegistry<CalculatorRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(AlgorithmSeparatorRecipes.instance().getRecipes()::remove);
        restoreFromBackup().forEach(AlgorithmSeparatorRecipes.instance()::addRecipe);
    }

    public void add(CalculatorRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        AlgorithmSeparatorRecipes.instance().getRecipes().add(recipe);
    }

    public boolean remove(CalculatorRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        AlgorithmSeparatorRecipes.instance().getRecipes().remove(recipe);
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('calculator:tanzaniteleaves')"))
    public boolean removeByInput(ItemStack input) {
        return AlgorithmSeparatorRecipes.instance().getRecipes().removeIf(recipe -> {
            for (ISonarRecipeObject recipeInput : recipe.inputs()) {
                for (ItemStack itemStack : recipeInput.getJEIValue()) {
                    if (ItemHandlerHelper.canItemStacksStack(itemStack, input)) {
                        addBackup(recipe);
                        return true;
                    }
                }
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('calculator:weakeneddiamond')"))
    public boolean removeByOutput(ItemStack output) {
        return AlgorithmSeparatorRecipes.instance().getRecipes().removeIf(r -> {
            for (ISonarRecipeObject recipeOutput : r.recipeOutputs) {
                for (ItemStack itemStack : recipeOutput.getJEIValue()) {
                    if (ItemHandlerHelper.canItemStacksStack(itemStack, output)) {
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
        AlgorithmSeparatorRecipes.instance().getRecipes().forEach(x -> addBackup(x));
        AlgorithmSeparatorRecipes.instance().getRecipes().clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<CalculatorRecipe> streamRecipes() {
        return new SimpleObjectStream<>(AlgorithmSeparatorRecipes.instance().getRecipes())
                .setRemover(this::remove);
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "2", type = Comp.Type.LTE)})
    public static class RecipeBuilder extends AbstractRecipeBuilder<CalculatorRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Calculator Algorithm Separator Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 2);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CalculatorRecipe register() {
            if (!validate()) return null;

            CalculatorRecipe recipe = AlgorithmSeparatorRecipes.instance()
                    .buildDefaultRecipe(IngredientHelper.toIngredientNonNullList(input), output, new ArrayList<>(), false);

            ModSupport.CALCULATOR.get().algorithmSeparator.add(recipe);
            return recipe;
        }
    }
}