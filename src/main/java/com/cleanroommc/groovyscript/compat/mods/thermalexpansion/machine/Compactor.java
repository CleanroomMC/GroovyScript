package com.cleanroommc.groovyscript.compat.mods.thermalexpansion.machine;

import cofh.core.inventory.ComparableItemStackValidatedNBT;
import cofh.thermalexpansion.util.managers.machine.CompactorManager;
import cofh.thermalexpansion.util.managers.machine.CompactorManager.CompactorRecipe;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.CompactorManagerAccessor;
import com.cleanroommc.groovyscript.core.mixin.thermalexpansion.CompactorRecipeAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@RegistryDescription
public class Compactor extends VirtualizedRegistry<Pair<CompactorManager.Mode, CompactorRecipe>> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond') * 2).mode(mode('coin'))"),
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond')).mode(mode('all'))"),
            @Example(".input(item('minecraft:diamond') * 2).output(item('minecraft:gold_ingot')).mode(mode('plate')).energy(1000)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> map(recipe.getKey()).values().removeIf(r -> r == recipe.getValue()));
        restoreFromBackup().forEach(r -> map(r.getKey()).put(new ComparableItemStackValidatedNBT(r.getValue().getInput()), r.getValue()));
    }

    private Map<ComparableItemStackValidatedNBT, CompactorRecipe> map(CompactorManager.Mode mode) {
        return switch (mode) {
            case ALL -> CompactorManagerAccessor.getRecipeMapAll();
            case PLATE -> CompactorManagerAccessor.getRecipeMapPlate();
            case COIN -> CompactorManagerAccessor.getRecipeMapCoin();
            case GEAR -> CompactorManagerAccessor.getRecipeMapGear();
        };
    }

    public void add(CompactorManager.Mode mode, CompactorRecipe recipe) {
        map(mode).put(new ComparableItemStackValidatedNBT(recipe.getInput()), recipe);
        addScripted(Pair.of(mode, recipe));
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "1000, mode('plate'), item('minecraft:obsidian') * 2, item('minecraft:gold_ingot')", commented = true))
    public CompactorRecipe add(int energy, CompactorManager.Mode mode, IIngredient input, ItemStack output) {
        return recipeBuilder()
                .energy(energy)
                .mode(mode)
                .input(input)
                .output(output)
                .register();
    }

    public boolean remove(CompactorManager.Mode mode, CompactorRecipe recipe) {
        return map(mode).values().removeIf(r -> {
            if (r == recipe) {
                addBackup(Pair.of(mode, recipe));
                return true;
            }
            return false;
        });
    }

    public boolean remove(CompactorRecipe recipe) {
        boolean hasRemoved = false;
        for (CompactorManager.Mode mode : CompactorManager.Mode.values()) {
            hasRemoved = remove(mode, recipe) || hasRemoved;
        }
        return hasRemoved;
    }

    @MethodDescription(example = @Example("mode('coin'), item('thermalfoundation:material:130')"))
    public boolean removeByInput(CompactorManager.Mode mode, IIngredient input) {
        return map(mode).values().removeIf(r -> {
            if (input.test(r.getInput())) {
                addBackup(Pair.of(mode, r));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:iron_ingot')"))
    public boolean removeByInput(IIngredient input) {
        boolean hasRemoved = false;
        for (CompactorManager.Mode mode : CompactorManager.Mode.values()) {
            hasRemoved = removeByInput(mode, input) || hasRemoved;
        }
        return hasRemoved;
    }

    @MethodDescription(example = @Example("mode('coin'), item('thermalfoundation:coin:102')"))
    public boolean removeByOutput(CompactorManager.Mode mode, IIngredient output) {
        return map(mode).values().removeIf(r -> {
            if (output.test(r.getOutput())) {
                addBackup(Pair.of(mode, r));
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = {
            @Example("item('thermalfoundation:material:24')"), @Example("item('minecraft:blaze_rod')")
    })
    public boolean removeByOutput(IIngredient output) {
        boolean hasRemoved = false;
        for (CompactorManager.Mode mode : CompactorManager.Mode.values()) {
            hasRemoved = removeByOutput(mode, output) || hasRemoved;
        }
        return hasRemoved;
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<CompactorRecipe> streamRecipes() {
        return new SimpleObjectStream<>(Arrays.stream(CompactorManager.Mode.values()).map(this::map).map(Map::values).flatMap(Collection::stream).collect(Collectors.toList()))
                .setRemover(this::remove);
    }

    @MethodDescription(example = @Example(value = "mode('plate')", commented = true))
    public void removeByMode(CompactorManager.Mode mode) {
        map(mode).values().forEach(x -> addBackup(Pair.of(mode, x)));
        map(mode).clear();
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        Arrays.stream(CompactorManager.Mode.values()).forEach(this::removeByMode);
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<CompactorRecipe> {

        @Property(defaultValue = "CompactorManager.Mode.ALL")
        CompactorManager.Mode mode = CompactorManager.Mode.ALL;
        @Property(defaultValue = "CompactorManager.DEFAULT_ENERGY", valid = @Comp(value = "0", type = Comp.Type.GT), value = "groovyscript.wiki.thermalexpansion.energy.value")
        private int energy = CompactorManager.DEFAULT_ENERGY;

        @RecipeBuilderMethodDescription
        public RecipeBuilder energy(int energy) {
            this.energy = energy;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder mode(CompactorManager.Mode mode) {
            this.mode = mode;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Thermal Expansion Compactor recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(energy <= 0, "energy must be greater than 0, yet it was {}", energy);
            msg.add(mode == null, "mode must be defined");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CompactorRecipe register() {
            if (!validate()) return null;
            CompactorRecipe recipe = null;

            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                CompactorRecipe recipe1 = CompactorRecipeAccessor.createCompactorRecipe(itemStack, output.get(0), energy);
                ModSupport.THERMAL_EXPANSION.get().compactor.add(mode, recipe1);
                if (recipe == null) recipe = recipe1;
            }
            return recipe;
        }
    }
}
