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
import sonar.calculator.mod.common.recipes.RedstoneExtractorRecipes;
import sonar.core.recipes.DefaultSonarRecipe;
import sonar.core.recipes.ISonarRecipeObject;

import java.util.Arrays;

@RegistryDescription
public class RedstoneExtractor extends VirtualizedRegistry<DefaultSonarRecipe.Value> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay')).value(100)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(RedstoneExtractorRecipes.instance().getRecipes()::remove);
        restoreFromBackup().forEach(RedstoneExtractorRecipes.instance().getRecipes()::add);
    }

    public void add(DefaultSonarRecipe.Value recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        RedstoneExtractorRecipes.instance().getRecipes().add(recipe);
    }

    public boolean remove(DefaultSonarRecipe.Value recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        RedstoneExtractorRecipes.instance().getRecipes().remove(recipe);
        return true;
    }

    @MethodDescription(example = @Example("item('minecraft:redstone_block')"))
    public boolean removeByInput(IIngredient input) {
        return RedstoneExtractorRecipes.instance().getRecipes().removeIf(r -> {
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
        RedstoneExtractorRecipes.instance().getRecipes().forEach(this::addBackup);
        RedstoneExtractorRecipes.instance().getRecipes().clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<DefaultSonarRecipe.Value> streamRecipes() {
        return new SimpleObjectStream<>(RedstoneExtractorRecipes.instance().getRecipes())
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
            return "Error adding Calculator Redstone Extractor Recipe";
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

            DefaultSonarRecipe.Value recipe = RedstoneExtractorRecipes.instance()
                    .buildDefaultRecipe(Calculator.toSonarRecipeObjectList(input), output, Arrays.asList(value), false);

            ModSupport.CALCULATOR.get().redstoneExtractor.add(recipe);
            return recipe;
        }
    }
}
