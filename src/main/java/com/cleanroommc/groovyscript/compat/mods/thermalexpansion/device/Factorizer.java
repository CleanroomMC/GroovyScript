package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.device;

import cofh.core.inventory.ComparableItemStackValidated;
import cofh.thermalexpansion.util.managers.device.FactorizerManager;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.FactorizerManagerAccessor;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.FactorizerRecipeAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.google.common.primitives.Booleans;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@RegistryDescription
public class Factorizer extends VirtualizedRegistry<Pair<Boolean, FactorizerManager.FactorizerRecipe>> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay') * 7).output(item('minecraft:book') * 2).combine().split()"),
            @Example(".input(item('minecraft:planks:*') * 4).output(item('minecraft:crafting_table')).combine()")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> map(recipe.getKey()).values().removeIf(r -> r.equals(recipe.getValue())));
        restoreFromBackup().forEach(r -> map(r.getKey()).put(new ComparableItemStackValidated(r.getValue().getInput()), r.getValue()));
    }

    private Map<ComparableItemStackValidated, FactorizerManager.FactorizerRecipe> map(boolean isSplit) {
        return isSplit ? FactorizerManagerAccessor.getRecipeMapReverse() : FactorizerManagerAccessor.getRecipeMap();
    }

    public void add(boolean isSplit, FactorizerManager.FactorizerRecipe recipe) {
        map(isSplit).put(new ComparableItemStackValidated(recipe.getInput()), recipe);
        addScripted(Pair.of(isSplit, recipe));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION)
    public FactorizerManager.FactorizerRecipe add(boolean combine, boolean split, IIngredient input, ItemStack output) {
        return recipeBuilder()
                .combine(combine)
                .split(split)
                .input(input)
                .output(output)
                .register();
    }

    public void add(FactorizerManager.FactorizerRecipe recipe) {
        add(true, recipe);
        add(false, recipe);
    }

    public boolean remove(boolean isSplit, FactorizerManager.FactorizerRecipe recipe) {
        return map(isSplit).values().removeIf(r -> {
            if (r == recipe) {
                addBackup(Pair.of(isSplit, recipe));
                return true;
            }
            return false;
        });
    }

    public boolean remove(FactorizerManager.FactorizerRecipe recipe) {
        boolean hasRemoved = remove(false, recipe);
        hasRemoved = remove(true, recipe) || hasRemoved;
        return hasRemoved;
    }

    @MethodDescription(example = @Example("false, item('minecraft:diamond')"))
    public boolean removeByInput(boolean isSplit, IIngredient input) {
        return map(isSplit).values().removeIf(r -> {
            if (input.test(r.getInput())) {
                addBackup(Pair.of(isSplit, r));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:coal:1')"))
    public boolean removeByInput(IIngredient input) {
        // done this way so both remove operations are called
        boolean hasRemoved = removeByInput(false, input);
        hasRemoved = removeByInput(true, input) || hasRemoved;
        return hasRemoved;
    }

    @MethodDescription(example = @Example(value = "false, item('minecraft:coal:1')", commented = true))
    public boolean removeByOutput(boolean isSplit, IIngredient output) {
        return map(isSplit).values().removeIf(r -> {
            if (output.test(r.getOutput())) {
                addBackup(Pair.of(isSplit, r));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('minecraft:emerald_block')"))
    public boolean removeByOutput(IIngredient output) {
        // done this way so both remove operations are called
        boolean hasRemoved = removeByOutput(false, output);
        hasRemoved = removeByOutput(true, output) || hasRemoved;
        return hasRemoved;
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<FactorizerManager.FactorizerRecipe> streamRecipes() {
        return new SimpleObjectStream<>(Booleans.asList(true, false).stream().map(this::map).map(Map::values).flatMap(Collection::stream).collect(Collectors.toList()))
                .setRemover(this::remove);
    }

    @MethodDescription(example = @Example(value = "true", commented = true))
    public void removeByType(boolean isSplit) {
        map(isSplit).values().forEach(x -> addBackup(Pair.of(isSplit, x)));
        map(isSplit).clear();
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        removeByType(true);
        removeByType(false);
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<FactorizerManager.FactorizerRecipe> {

        @Property
        private boolean combine;
        @Property
        private boolean split;

        @RecipeBuilderMethodDescription
        public RecipeBuilder combine(boolean combine) {
            this.combine = combine;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder combine() {
            this.combine = !combine;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder split(boolean split) {
            this.split = split;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder split() {
            this.split = !split;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thermal Expansion Factorizer recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable FactorizerManager.FactorizerRecipe register() {
            if (!validate()) return null;
            FactorizerManager.FactorizerRecipe recipe = null;

            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                if (combine) {
                    FactorizerManager.FactorizerRecipe recipe1 = FactorizerRecipeAccessor.createFactorizerRecipe(itemStack, output.get(0));
                    ModSupport.THERMAL_EXPANSION.get().factorizer.add(false, recipe1);
                    if (recipe == null) recipe = recipe1;
                }
                if (split) {
                    FactorizerManager.FactorizerRecipe recipe1 = FactorizerRecipeAccessor.createFactorizerRecipe(output.get(0), itemStack);
                    ModSupport.THERMAL_EXPANSION.get().factorizer.add(true, recipe1);
                    if (recipe == null) recipe = recipe1;
                }
            }
            return recipe;
        }
    }

}
