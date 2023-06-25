package com.cleanroommc.groovyscript.compat.mods.actuallyadditions;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
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

public class Compost extends VirtualizedRegistry<CompostRecipe> {

    public Compost() {
        super();
    }

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

    public boolean removeByInput(IIngredient input) {
        return ActuallyAdditionsAPI.COMPOST_RECIPES.removeIf(recipe -> {
            boolean found = recipe.getInput().test(IngredientHelper.toItemStack(input));
            if (found) {
                addBackup(recipe);
            }
            return found;
        });
    }

    public boolean removeByOutput(ItemStack output) {
        return ActuallyAdditionsAPI.COMPOST_RECIPES.removeIf(recipe -> {
            boolean matches = ItemStack.areItemStacksEqual(recipe.getOutput(), output);
            if (matches) {
                addBackup(recipe);
            }
            return matches;
        });
    }

    public void removeAll() {
        ActuallyAdditionsAPI.COMPOST_RECIPES.forEach(this::addBackup);
        ActuallyAdditionsAPI.COMPOST_RECIPES.clear();
    }

    public SimpleObjectStream<CompostRecipe> streamRecipes() {
        return new SimpleObjectStream<>(ActuallyAdditionsAPI.COMPOST_RECIPES)
                .setRemover(this::remove);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<CompostRecipe> {

        private IBlockState inputDisplay;
        private IBlockState outputDisplay;

        public RecipeBuilder inputDisplay(IBlockState inputDisplay) {
            this.inputDisplay = inputDisplay;
            return this;
        }

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
        public @Nullable CompostRecipe register() {
            if (!validate()) return null;
            CompostRecipe recipe = new CompostRecipe(input.get(0).toMcIngredient(), inputDisplay, output.get(0), outputDisplay);
            ModSupport.ACTUALLY_ADDITIONS.get().compost.add(recipe);
            return recipe;
        }
    }
}
