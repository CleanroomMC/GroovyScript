package com.cleanroommc.groovyscript.compat.mods.avaritia;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import morph.avaritia.recipe.AvaritiaRecipeManager;
import morph.avaritia.recipe.compressor.CompressorRecipe;
import morph.avaritia.recipe.compressor.ICompressorRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class Compressor extends VirtualizedRegistry<ICompressorRecipe> {

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> AvaritiaRecipeManager.COMPRESSOR_RECIPES.values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(recipe -> AvaritiaRecipeManager.COMPRESSOR_RECIPES.put(recipe.getRegistryName(), recipe));
    }

    public boolean remove(ICompressorRecipe recipe) {
        recipe = AvaritiaRecipeManager.COMPRESSOR_RECIPES.remove(recipe.getRegistryName());
        if (recipe != null) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public boolean removeByOutput(ItemStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing avaritia compressor recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
            return false;
        }
        return AvaritiaRecipeManager.COMPRESSOR_RECIPES.values().removeIf(recipe -> {
            if (recipe != null && recipe.getResult().isItemEqual(output)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    public void removeAll() {
        AvaritiaRecipeManager.COMPRESSOR_RECIPES.values().forEach(this::addBackup);
        AvaritiaRecipeManager.COMPRESSOR_RECIPES.values().clear();
    }

    public SimpleObjectStream<ICompressorRecipe> streamRecipes() {
        return new SimpleObjectStream<>(AvaritiaRecipeManager.COMPRESSOR_RECIPES.values()).setRemover(this::remove);
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public void add(ICompressorRecipe recipe) {
        AvaritiaRecipeManager.COMPRESSOR_RECIPES.put(recipe.getRegistryName(), recipe);
        addScripted(recipe);
    }

    public void add(ItemStack output, IIngredient input, int cost) {
        recipeBuilder()
                .inputCount(cost)
                .input(input)
                .output(output)
                .register();
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<ICompressorRecipe> {

        private int inputCount = 300;

        @Override
        public AbstractRecipeBuilder<ICompressorRecipe> input(IIngredient ingredient) {
            if (ingredient == null) return this;
            if (ingredient.getAmount() > 1) {
                this.inputCount = ingredient.getAmount();
            }
            return super.input(ingredient.withAmount(1));
        }

        public RecipeBuilder inputCount(int inputCount) {
            this.inputCount = inputCount;
            return this;
        }

        @Override
        public String getRecipeNamePrefix() {
            return "avaritia_compressor_";
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Avaritia compressor recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            validateName();
            if (this.inputCount <= 0) {
                this.inputCount = 1;
            }
        }

        @Override
        public @Nullable ICompressorRecipe register() {
            if (!validate()) return null;
            CompressorRecipe recipe = new CompressorRecipe(this.output.get(0), this.inputCount, true, Collections.singletonList(this.input.get(0).toMcIngredient()));
            recipe.setRegistryName(this.name);
            add(recipe);
            return recipe;
        }
    }
}
