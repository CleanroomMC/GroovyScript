package com.cleanroommc.groovyscript.compat.mods.calculator;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;
import sonar.calculator.mod.common.recipes.CalculatorRecipe;
import sonar.calculator.mod.common.recipes.ExtractionChamberRecipes;
import sonar.core.recipes.ISonarRecipeObject;

import java.util.ArrayList;
import java.util.List;

@RegistryDescription
public class ExtractionChamber extends VirtualizedRegistry<CalculatorRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(ExtractionChamberRecipes.instance().getRecipes()::remove);
        restoreFromBackup().forEach(ExtractionChamberRecipes.instance().getRecipes()::add);
    }

    public void add(CalculatorRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        ExtractionChamberRecipes.instance().getRecipes().add(recipe);
    }

    public boolean remove(CalculatorRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ExtractionChamberRecipes.instance().getRecipes().remove(recipe);
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('minecraft:dirt')"))
    public boolean removeByInput(ItemStack input) {
        return ExtractionChamberRecipes.instance().getRecipes().removeIf(r -> {
            for (ISonarRecipeObject recipeInput : r.recipeInputs) {
                for (ItemStack itemStack : recipeInput.getJEIValue()) {
                    if (ItemHandlerHelper.canItemStacksStack(itemStack, input)) {
                        addBackup(r);
                        return true;
                    }
                }
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('calculator:smallstone')"))
    public boolean removeByOutput(ItemStack output) {
        return ExtractionChamberRecipes.instance().getRecipes().removeIf(r -> {
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
        ExtractionChamberRecipes.instance().getRecipes().forEach(this::addBackup);
        ExtractionChamberRecipes.instance().getRecipes().clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<CalculatorRecipe> streamRecipes() {
        return new SimpleObjectStream<>(ExtractionChamberRecipes.instance().getRecipes())
                .setRemover(this::remove);
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "2", type = Comp.Type.LTE)})
    public static class RecipeBuilder extends AbstractRecipeBuilder<CalculatorRecipe> {

        @Property
        private boolean hasChance;

        @RecipeBuilderMethodDescription
        public RecipeBuilder hasChance() {
            this.hasChance = !hasChance;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder hasChance(boolean hasChance) {
            this.hasChance = hasChance;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Calculator Extraction Chamber Recipe";
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

            List<Object> specialOutput = new ArrayList<>();
            specialOutput.add(output.get(0));
            if (output.size() == 2 && hasChance) specialOutput.add(new ExtractionChamberRecipes.ExtractionChamberOutput(output.get(1)));

            CalculatorRecipe recipe = ExtractionChamberRecipes.instance()
                    .buildDefaultRecipe(Calculator.toSonarRecipeObjectList(input), specialOutput, new ArrayList<>(), false);

            ModSupport.CALCULATOR.get().extractionChamber.add(recipe);
            return recipe;
        }
    }
}
