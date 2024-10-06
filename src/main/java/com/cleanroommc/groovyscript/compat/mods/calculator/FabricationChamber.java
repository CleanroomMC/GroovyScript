package com.cleanroommc.groovyscript.compat.mods.calculator;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;
import sonar.calculator.mod.common.recipes.FabricationChamberRecipes;
import sonar.calculator.mod.common.recipes.FabricationSonarRecipe;
import sonar.core.recipes.ISonarRecipeObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RegistryDescription
public class FabricationChamber extends StandardListRegistry<FabricationSonarRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('calculator:circuitboard:8').withNbt([Stable: 0, Analysed: 1])).output(item('minecraft:diamond'))"),
            @Example(".input(item('calculator:circuitboard:0').withNbt([Stable: 0, Analysed: true]), item('calculator:circuitboard:1').withNbt([Stable: 0, Analysed: true]), item('calculator:circuitboard:2').withNbt([Stable: 0, Analysed: true]), item('calculator:circuitboard:3').withNbt([Stable: 0, Analysed: true]), item('calculator:circuitboard:4').withNbt([Stable: 0, Analysed: true])).input(item('calculator:circuitboard:0').withNbt([Stable: 1, Analysed: true]), item('calculator:circuitboard:1').withNbt([Stable: 1, Analysed: true]), item('calculator:circuitboard:2').withNbt([Stable: 1, Analysed: true]), item('calculator:circuitboard:3').withNbt([Stable: 1, Analysed: true]), item('calculator:circuitboard:4').withNbt([Stable: 1, Analysed: true])).output(item('minecraft:clay'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<FabricationSonarRecipe> getRecipes() {
        return FabricationChamberRecipes.instance().getRecipes();
    }

    @MethodDescription(example = @Example("item('calculator:circuitboard:8').withNbt([Stable: 0, Analysed: 1])"))
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

    @MethodDescription(example = @Example("item('calculator:calculatorassembly')"))
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

    @Property(property = "input", comp = @Comp(gte = 1))
    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<FabricationSonarRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Calculator Fabrication Chamber Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, Integer.MAX_VALUE, 1, 1);
            validateFluids(msg);
            // I love streams, but this sequence is a bit short for my tastes.
            List<String> inputNames = input.stream()
                    .map(IngredientHelper::toItemStack)
                    .map(ItemStack::getItem)
                    .map(IForgeRegistryEntry.Impl::getRegistryName)
                    .filter(Objects::nonNull)
                    .map(ResourceLocation::toString)
                    .filter(x -> !x.contains("calculator:circuitboard"))
                    .collect(Collectors.toList());
            msg.add(!inputNames.isEmpty(), "All inputs must be an item of 'calculator:circuitboard', found {}", String.join(", ", inputNames));
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable FabricationSonarRecipe register() {
            if (!validate()) return null;

            FabricationSonarRecipe recipe = FabricationChamberRecipes.instance()
                    .buildDefaultRecipe(Calculator.toSonarRecipeObjectList(input), output, new ArrayList<>(), true);

            ModSupport.CALCULATOR.get().fabricationChamber.add(recipe);
            return recipe;
        }

    }
}
