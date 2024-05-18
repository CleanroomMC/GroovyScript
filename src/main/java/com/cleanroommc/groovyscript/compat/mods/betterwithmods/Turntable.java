package com.cleanroommc.groovyscript.compat.mods.betterwithmods;

import betterwithmods.common.BWRegistry;
import betterwithmods.common.registry.block.recipe.BlockIngredient;
import betterwithmods.common.registry.block.recipe.TurntableRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@RegistryDescription
public class Turntable extends VirtualizedRegistry<TurntableRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:gold_block')).outputBlock(blockstate('minecraft:clay')).output(item('minecraft:gold_ingot') * 5).rotations(5)"),
            @Example(".input(item('minecraft:clay')).output(item('minecraft:gold_ingot')).rotations(2)")
    })
        @RecipeBuilderMethodDescription
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> BWRegistry.TURNTABLE.getRecipes().removeIf(r -> r == recipe));
        BWRegistry.TURNTABLE.getRecipes().addAll(restoreFromBackup());
    }

    public TurntableRecipe add(TurntableRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            BWRegistry.TURNTABLE.getRecipes().add(recipe);
        }
        return recipe;
    }

    public boolean remove(TurntableRecipe recipe) {
        if (BWRegistry.TURNTABLE.getRecipes().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example("item('minecraft:clay_ball')"))
    public boolean removeByOutput(IIngredient output) {
        return BWRegistry.TURNTABLE.getRecipes().removeIf(r -> {
            for (ItemStack itemstack : r.getOutputs()) {
                if (output.test(itemstack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('betterwithmods:unfired_pottery')"))
    public boolean removeByInput(IIngredient input) {
        return BWRegistry.TURNTABLE.getRecipes().removeIf(r -> {
            for (ItemStack itemstack : r.getInput().getMatchingStacks()) {
                if (input.test(itemstack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<TurntableRecipe> streamRecipes() {
        return new SimpleObjectStream<>(BWRegistry.TURNTABLE.getRecipes()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        BWRegistry.TURNTABLE.getRecipes().forEach(this::addBackup);
        BWRegistry.TURNTABLE.getRecipes().clear();
    }

    @Property(property = "output", valid = {@Comp(value = "0", type = Comp.Type.GTE), @Comp(value = "2", type = Comp.Type.LTE)})
    public static class RecipeBuilder extends AbstractRecipeBuilder<TurntableRecipe> {

        @Property
        private BlockIngredient input;
        @Property(defaultValue = "Blocks.AIR.getDefaultState()")
        private IBlockState outputBlock = Blocks.AIR.getDefaultState();
        @Property(defaultValue = "1")
        private int rotations = 1;

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
        public RecipeBuilder outputBlock(IBlockState outputBlock) {
            this.outputBlock = outputBlock;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder rotations(int rotations) {
            this.rotations = rotations;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Better With Mods Turntable recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 0, 2);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable TurntableRecipe register() {
            if (!validate()) return null;

            TurntableRecipe recipe = new TurntableRecipe(input, output, outputBlock, rotations);
            ModSupport.BETTER_WITH_MODS.get().turntable.add(recipe);
            return recipe;
        }
    }

}
