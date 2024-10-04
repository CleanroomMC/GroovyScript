package com.cleanroommc.groovyscript.compat.mods.calculator;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import sonar.calculator.mod.common.recipes.ConductorMastRecipes;
import sonar.core.recipes.DefaultSonarRecipe;
import sonar.core.recipes.ISonarRecipeObject;

import java.util.Arrays;
import java.util.Collection;

@RegistryDescription
public class ConductorMast extends StandardListRegistry<DefaultSonarRecipe.Value> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond')).value(100)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<DefaultSonarRecipe.Value> getRecipes() {
        return ConductorMastRecipes.instance().getRecipes();
    }

    @MethodDescription(example = @Example("item('calculator:firediamond')"))
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

    @MethodDescription(example = @Example("item('calculator:material:7')"))
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
    public static class RecipeBuilder extends AbstractRecipeBuilder<DefaultSonarRecipe.Value> {

        @Property(comp = @Comp(gte = 1))
        private int value;

        @RecipeBuilderMethodDescription
        public RecipeBuilder value(int value) {
            this.value = value;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Calculator Conductor Mast Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(value <= 0, "value must be greater than or equal to 1, yet it was {}", value);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable DefaultSonarRecipe.Value register() {
            if (!validate()) return null;

            DefaultSonarRecipe.Value recipe = ConductorMastRecipes.instance()
                    .buildDefaultRecipe(Calculator.toSonarRecipeObjectList(input), output, Arrays.asList(value), false);

            ModSupport.CALCULATOR.get().conductorMast.add(recipe);
            return recipe;
        }
    }
}
