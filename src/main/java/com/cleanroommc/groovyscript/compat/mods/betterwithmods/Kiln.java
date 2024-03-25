package com.cleanroommc.groovyscript.compat.mods.betterwithmods;

import betterwithmods.common.BWRegistry;
import betterwithmods.common.registry.block.recipe.BlockIngredient;
import betterwithmods.common.registry.block.recipe.KilnRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@RegistryDescription
public class Kiln extends VirtualizedRegistry<KilnRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond')).heat(2)"),
            @Example(".input(item('minecraft:diamond_block')).output(item('minecraft:gold_ingot') * 16).ignoreHeat()")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> BWRegistry.KILN.getRecipes().removeIf(r -> r == recipe));
        BWRegistry.KILN.getRecipes().addAll(restoreFromBackup());
    }

    public KilnRecipe add(KilnRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            BWRegistry.KILN.getRecipes().add(recipe);
        }
        return recipe;
    }

    public boolean remove(KilnRecipe recipe) {
        if (BWRegistry.KILN.getRecipes().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('minecraft:brick')"))
    public boolean removeByOutput(IIngredient output) {
        return BWRegistry.KILN.getRecipes().removeIf(r -> {
            for (ItemStack itemstack : r.getOutputs()) {
                if (output.test(itemstack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('minecraft:end_stone')"))
    public boolean removeByInput(IIngredient input) {
        return BWRegistry.KILN.getRecipes().removeIf(r -> {
            for (ItemStack itemstack : r.getInput().getMatchingStacks()) {
                if (input.test(itemstack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.streamRecipes", type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<KilnRecipe> streamRecipes() {
        return new SimpleObjectStream<>(BWRegistry.KILN.getRecipes()).setRemover(this::remove);
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        BWRegistry.KILN.getRecipes().forEach(this::addBackup);
        BWRegistry.KILN.getRecipes().clear();
    }

    @Property(property = "output", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "3", type = Comp.Type.LTE)})
    public static class RecipeBuilder extends AbstractRecipeBuilder<KilnRecipe> {

        @Property
        private BlockIngredient input;
        @Property(defaultValue = "1")
        private int heat = 1;
        @Property
        private boolean ignoreHeat;

        @RecipeBuilderMethodDescription
        public RecipeBuilder input(BlockIngredient input) {
            this.input = input;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder input(String input) {
            this.input = new BlockIngredient(input);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder input(List<ItemStack> input) {
            this.input = new BlockIngredient(input);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder input(ItemStack... input) {
            this.input = new BlockIngredient(input);
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder input(IIngredient input) {
            this.input = new BlockIngredient(input.toMcIngredient());
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder heat(int heat) {
            this.heat = heat;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder ignoreHeat(boolean ignoreHeat) {
            this.ignoreHeat = ignoreHeat;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder ignoreHeat() {
            this.ignoreHeat = !ignoreHeat;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Better With Mods Kiln recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 3);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable KilnRecipe register() {
            if (!validate()) return null;

            KilnRecipe recipe = new KilnRecipe(input, output, heat);
            recipe.setIgnoreHeat(ignoreHeat);
            ModSupport.BETTER_WITH_MODS.get().kiln.add(recipe);
            return recipe;
        }
    }

}
