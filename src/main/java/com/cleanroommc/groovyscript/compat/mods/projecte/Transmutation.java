package com.cleanroommc.groovyscript.compat.mods.projecte;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.jei.removal.IJEIRemoval;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import moze_intel.projecte.integration.jei.world_transmute.WorldTransmuteRecipeCategory;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

@RegistryDescription
public class Transmutation extends VirtualizedRegistry<WorldTransmutations.Entry> implements IJEIRemoval.Default {

    @RecipeBuilderDescription(example = {
            @Example(".input(blockstate('minecraft:end_stone')).output(blockstate('minecraft:diamond_block'), blockstate('minecraft:gold_block'))"),
            @Example(".input(blockstate('minecraft:diamond_block')).output(blockstate('minecraft:end_stone')).altOutput(blockstate('minecraft:gold_block'))"),
            @Example(".input(blockstate('minecraft:gold_block')).output(blockstate('minecraft:diamond_block'))")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        WorldTransmutations.getWorldTransmutations().removeAll(removeScripted());
        WorldTransmutations.getWorldTransmutations().addAll(restoreFromBackup());
    }

    public void add(WorldTransmutations.Entry recipe) {
        addScripted(recipe);
        WorldTransmutations.getWorldTransmutations().add(recipe);
    }

    public boolean remove(WorldTransmutations.Entry recipe) {
        if (WorldTransmutations.getWorldTransmutations().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example("blockstate('minecraft:wool')"))
    public boolean removeByInput(IBlockState input) {
        return WorldTransmutations.getWorldTransmutations().removeIf(r -> {
            if (input.equals(r.input)) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("blockstate('minecraft:dirt')"))
    public boolean removeByOutput(IBlockState output) {
        return WorldTransmutations.getWorldTransmutations().removeIf(r -> {
            if (output.equals(r.outputs.getKey()) || output.equals(r.outputs.getValue())) {
                addBackup(r);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<WorldTransmutations.Entry> streamRecipes() {
        return new SimpleObjectStream<>(WorldTransmutations.getWorldTransmutations()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        WorldTransmutations.getWorldTransmutations().forEach(this::addBackup);
        WorldTransmutations.getWorldTransmutations().clear();
    }

    @Override
    public @NotNull Collection<String> getCategories() {
        return Collections.singletonList(WorldTransmuteRecipeCategory.UID);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<WorldTransmutations.Entry> {

        @Property(valid = @Comp(value = "null", type = Comp.Type.NOT), ignoresInheritedMethods = true)
        IBlockState input;
        @Property(valid = @Comp(value = "null", type = Comp.Type.NOT), ignoresInheritedMethods = true)
        IBlockState output;
        @Property
        IBlockState altOutput;

        @RecipeBuilderMethodDescription
        public RecipeBuilder input(IBlockState input) {
            this.input = input;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder output(IBlockState output) {
            this.output = output;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder altOutput(IBlockState altOutput) {
            this.altOutput = altOutput;
            return this;
        }

        @RecipeBuilderMethodDescription(field = {"output", "altOutput"})
        public RecipeBuilder output(IBlockState output, IBlockState altOutput) {
            this.output = output;
            this.altOutput = altOutput;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder input(Block input) {
            this.input = input.getDefaultState();
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder output(Block output) {
            this.output = output.getDefaultState();
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder altOutput(Block altOutput) {
            this.altOutput = altOutput.getDefaultState();
            return this;
        }

        @RecipeBuilderMethodDescription(field = {"output", "altOutput"})
        public RecipeBuilder output(Block output, Block altOutput) {
            this.output = output.getDefaultState();
            this.altOutput = altOutput.getDefaultState();
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding ProjectE Transmutation recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg);
            msg.add(input == null, "input must not be null");
            msg.add(output == null, "output must not be null");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable WorldTransmutations.Entry register() {
            if (!validate()) return null;
            WorldTransmutations.Entry recipe = new WorldTransmutations.Entry(input, ImmutablePair.of(output, altOutput));
            ModSupport.PROJECT_E.get().transmutation.add(recipe);
            return recipe;
        }
    }

}
