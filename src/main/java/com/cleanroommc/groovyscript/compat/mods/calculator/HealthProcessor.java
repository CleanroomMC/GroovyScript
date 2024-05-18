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
import sonar.calculator.mod.common.recipes.HealthProcessorRecipes;
import sonar.core.recipes.DefaultSonarRecipe;
import sonar.core.recipes.ISonarRecipeObject;

import java.util.Arrays;

@RegistryDescription
public class HealthProcessor extends VirtualizedRegistry<DefaultSonarRecipe.Value> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay')).value(100)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(HealthProcessorRecipes.instance().getRecipes()::remove);
        restoreFromBackup().forEach(HealthProcessorRecipes.instance().getRecipes()::add);
    }

    public void add(DefaultSonarRecipe.Value recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        HealthProcessorRecipes.instance().getRecipes().add(recipe);
    }

    public boolean remove(DefaultSonarRecipe.Value recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        HealthProcessorRecipes.instance().getRecipes().remove(recipe);
        return true;
    }

    @MethodDescription(example = @Example("item('minecraft:blaze_rod')"))
    public boolean removeByInput(IIngredient input) {
        return HealthProcessorRecipes.instance().getRecipes().removeIf(r -> {
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

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        HealthProcessorRecipes.instance().getRecipes().forEach(this::addBackup);
        HealthProcessorRecipes.instance().getRecipes().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<DefaultSonarRecipe.Value> streamRecipes() {
        return new SimpleObjectStream<>(HealthProcessorRecipes.instance().getRecipes())
                .setRemover(this::remove);
    }

    @Property(property = "input", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<DefaultSonarRecipe.Value> {

        @Property(valid = @Comp(value = "1", type = Comp.Type.GTE))
        private int value;

        @RecipeBuilderMethodDescription
        public RecipeBuilder value(int value) {
            this.value = value;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Calculator Health Processor Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg);
            msg.add(value <= 0, "value must be greater than or equal to 1, yet it was {}", value);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable DefaultSonarRecipe.Value register() {
            if (!validate()) return null;

            DefaultSonarRecipe.Value recipe = HealthProcessorRecipes.instance()
                    .buildDefaultRecipe(Calculator.toSonarRecipeObjectList(input), output, Arrays.asList(value), false);

            ModSupport.CALCULATOR.get().healthProcessor.add(recipe);
            return recipe;
        }
    }
}
