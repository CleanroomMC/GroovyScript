package com.cleanroommc.groovyscript.compat.mods.actuallyadditions;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.CompostRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

@RegistryDescription
public class Compost extends VirtualizedRegistry<CompostRecipe> {

    @RecipeBuilderDescription(example = @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond')).inputDisplay(blockstate('minecraft:clay')).outputDisplay(blockstate('minecraft:diamond_block'))"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(ActuallyAdditionsAPI.COMPOST_RECIPES::remove);
        ActuallyAdditionsAPI.COMPOST_RECIPES.addAll(restoreFromBackup());
    }

    public CompostRecipe add(Ingredient input, Block inputDisplay, ItemStack output, Block outputDisplay) {
        return add(input, inputDisplay.getDefaultState(), output, outputDisplay.getDefaultState());
    }

    public CompostRecipe add(Ingredient input, IBlockState inputDisplay, ItemStack output, IBlockState outputDisplay) {
        CompostRecipe recipe = new CompostRecipe(input, inputDisplay, output, outputDisplay);
        add(recipe);
        return recipe;
    }

    public void add(CompostRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        ActuallyAdditionsAPI.COMPOST_RECIPES.add(recipe);
    }

    public boolean remove(CompostRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ActuallyAdditionsAPI.COMPOST_RECIPES.remove(recipe);
        return true;
    }

    @MethodDescription(example = @Example("item('actuallyadditions:item_canola_seed')"))
    public boolean removeByInput(IIngredient input) {
        return ActuallyAdditionsAPI.COMPOST_RECIPES.removeIf(recipe -> {
            boolean found = recipe.getInput().test(IngredientHelper.toItemStack(input));
            if (found) {
                addBackup(recipe);
            }
            return found;
        });
    }

    @MethodDescription(example = @Example("item('actuallyadditions:item_fertilizer')"))
    public boolean removeByOutput(ItemStack output) {
        return ActuallyAdditionsAPI.COMPOST_RECIPES.removeIf(recipe -> {
            boolean matches = ItemStack.areItemStacksEqual(recipe.getOutput(), output);
            if (matches) {
                addBackup(recipe);
            }
            return matches;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        ActuallyAdditionsAPI.COMPOST_RECIPES.forEach(this::addBackup);
        ActuallyAdditionsAPI.COMPOST_RECIPES.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<CompostRecipe> streamRecipes() {
        return new SimpleObjectStream<>(ActuallyAdditionsAPI.COMPOST_RECIPES)
                .setRemover(this::remove);
    }

    @Property(property = "input", valid = @Comp("1"))
    @Property(property = "output", valid = @Comp("1"))
    public static class RecipeBuilder extends AbstractRecipeBuilder<CompostRecipe> {

        @Property(property = "inputDisplay", valid = @Comp(type = Comp.Type.NOT, value = "null"))
        private IBlockState inputDisplay;
        @Property(property = "outputDisplay", valid = @Comp(type = Comp.Type.NOT, value = "null"))
        private IBlockState outputDisplay;

        @RecipeBuilderMethodDescription
        public RecipeBuilder inputDisplay(IBlockState inputDisplay) {
            this.inputDisplay = inputDisplay;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder outputDisplay(IBlockState outputDisplay) {
            this.outputDisplay = outputDisplay;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Actually Additions Compost recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(inputDisplay == null, "inputDisplay must be defined");
            msg.add(outputDisplay == null, "inputDisplay must be defined");
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CompostRecipe register() {
            if (!validate()) return null;
            CompostRecipe recipe = new CompostRecipe(input.get(0).toMcIngredient(), inputDisplay, output.get(0), outputDisplay);
            ModSupport.ACTUALLY_ADDITIONS.get().compost.add(recipe);
            return recipe;
        }
    }
}
