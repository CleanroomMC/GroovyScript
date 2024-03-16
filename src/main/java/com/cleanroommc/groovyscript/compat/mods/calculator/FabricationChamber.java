package com.cleanroommc.groovyscript.compat.mods.calculator;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;
import sonar.calculator.mod.common.recipes.FabricationChamberRecipes;
import sonar.calculator.mod.common.recipes.FabricationSonarRecipe;
import sonar.core.recipes.ISonarRecipeObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RegistryDescription
public class FabricationChamber extends VirtualizedRegistry<FabricationSonarRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('calculator:circuitboard:8').withNbt([Stable: 0, Analysed: 1])).output(item('minecraft:diamond'))"),
            @Example(".input(item('calculator:circuitboard:0').withNbt([Stable: 0, Analysed: true]), item('calculator:circuitboard:1').withNbt([Stable: 0, Analysed: true]), item('calculator:circuitboard:2').withNbt([Stable: 0, Analysed: true]), item('calculator:circuitboard:3').withNbt([Stable: 0, Analysed: true]), item('calculator:circuitboard:4').withNbt([Stable: 0, Analysed: true])).input(item('calculator:circuitboard:0').withNbt([Stable: 1, Analysed: true]), item('calculator:circuitboard:1').withNbt([Stable: 1, Analysed: true]), item('calculator:circuitboard:2').withNbt([Stable: 1, Analysed: true]), item('calculator:circuitboard:3').withNbt([Stable: 1, Analysed: true]), item('calculator:circuitboard:4').withNbt([Stable: 1, Analysed: true])).output(item('minecraft:clay'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(FabricationChamberRecipes.instance().getRecipes()::remove);
        restoreFromBackup().forEach(FabricationChamberRecipes.instance().getRecipes()::add);
    }

    public void add(FabricationSonarRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        FabricationChamberRecipes.instance().getRecipes().add(recipe);
    }

    public boolean remove(FabricationSonarRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        FabricationChamberRecipes.instance().getRecipes().remove(recipe);
        return true;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('calculator:circuitboard:8').withNbt([Stable: 0, Analysed: 1])"))
    public boolean removeByInput(IIngredient input) {
        return FabricationChamberRecipes.instance().getRecipes().removeIf(r -> {
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

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('calculator:calculatorassembly')"))
    public boolean removeByOutput(IIngredient output) {
        return FabricationChamberRecipes.instance().getRecipes().removeIf(r -> {
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
        FabricationChamberRecipes.instance().getRecipes().forEach(this::addBackup);
        FabricationChamberRecipes.instance().getRecipes().clear();
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<FabricationSonarRecipe> streamRecipes() {
        return new SimpleObjectStream<>(FabricationChamberRecipes.instance().getRecipes())
                .setRemover(this::remove);
    }

    @Property(property = "input", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "Integer.MAX_VALUE", type = Comp.Type.LTE)})
    @Property(property = "output", valid = @Comp("1"))
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
