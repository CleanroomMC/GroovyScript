package com.cleanroommc.groovyscript.compat.mods.betterwithmods;

import betterwithmods.common.BWRegistry;
import betterwithmods.common.registry.block.recipe.BlockIngredient;
import betterwithmods.common.registry.block.recipe.SawRecipe;
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
public class Saw extends VirtualizedRegistry<SawRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:diamond_block')).output(item('minecraft:gold_ingot') * 16)"))
        @RecipeBuilderMethodDescription
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> BWRegistry.WOOD_SAW.getRecipes().removeIf(r -> r == recipe));
        BWRegistry.WOOD_SAW.getRecipes().addAll(restoreFromBackup());
    }

    public SawRecipe add(SawRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            BWRegistry.WOOD_SAW.getRecipes().add(recipe);
        }
        return recipe;
    }

    public boolean remove(SawRecipe recipe) {
        if (BWRegistry.WOOD_SAW.getRecipes().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(description = "groovyscript.wiki.removeByOutput", example = @Example("item('minecraft:pumpkin')"))
    public boolean removeByOutput(IIngredient output) {
        return BWRegistry.WOOD_SAW.getRecipes().removeIf(r -> {
            for (ItemStack itemstack : r.getOutputs()) {
                if (output.test(itemstack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(description = "groovyscript.wiki.removeByInput", example = @Example("item('minecraft:vine')"))
    public boolean removeByInput(IIngredient input) {
        return BWRegistry.WOOD_SAW.getRecipes().removeIf(r -> {
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
    public SimpleObjectStream<SawRecipe> streamRecipes() {
        return new SimpleObjectStream<>(BWRegistry.WOOD_SAW.getRecipes()).setRemover(this::remove);
    }

    @MethodDescription(description = "groovyscript.wiki.removeAll", priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        BWRegistry.WOOD_SAW.getRecipes().forEach(this::addBackup);
        BWRegistry.WOOD_SAW.getRecipes().clear();
    }

    @Property(property = "output", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "9", type = Comp.Type.LTE)})
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
