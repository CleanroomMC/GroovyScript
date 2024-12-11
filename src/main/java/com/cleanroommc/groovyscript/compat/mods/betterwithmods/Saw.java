package com.cleanroommc.groovyscript.compat.mods.betterwithmods;

import betterwithmods.common.BWRegistry;
import betterwithmods.common.registry.block.recipe.BlockIngredient;
import betterwithmods.common.registry.block.recipe.SawRecipe;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

@RegistryDescription
public class Saw extends StandardListRegistry<SawRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond_block')).output(item('minecraft:gold_ingot') * 16)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<SawRecipe> getRecipes() {
        return BWRegistry.WOOD_SAW.getRecipes();
    }

    @MethodDescription(example = @Example("item('minecraft:pumpkin')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> {
            for (ItemStack itemstack : r.getOutputs()) {
                if (output.test(itemstack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:vine')"))
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> {
            for (ItemStack itemstack : r.getInput().getMatchingStacks()) {
                if (input.test(itemstack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @Property(property = "output", comp = @Comp(gte = 1, lte = 9))
    public static class RecipeBuilder extends AbstractRecipeBuilder<SawRecipe> {

        @Property
        private BlockIngredient input;

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

        @Override
        @RecipeBuilderMethodDescription
        public RecipeBuilder input(IIngredient input) {
            this.input = new BlockIngredient(input.toMcIngredient());
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Better With Mods Saw recipe";
        }

        @Override
        protected int getMaxItemInput() {
            // Uses blocks to craft
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 3);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable SawRecipe register() {
            if (!validate()) return null;

            SawRecipe recipe = new SawRecipe(input, output);
            ModSupport.BETTER_WITH_MODS.get().saw.add(recipe);
            return recipe;
        }
    }
}
