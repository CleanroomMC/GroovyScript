package com.cleanroommc.groovyscript.compat.mods.avaritia;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import morph.avaritia.recipe.AvaritiaRecipeManager;
import morph.avaritia.recipe.compressor.CompressorRecipe;
import morph.avaritia.recipe.compressor.ICompressorRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;

public class Compressor extends VirtualizedRegistry<ICompressorRecipe> {

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> AvaritiaRecipeManager.COMPRESSOR_RECIPES.values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(recipe -> AvaritiaRecipeManager.COMPRESSOR_RECIPES.put(recipe.getRegistryName(), recipe));
    }

    public void remove(ICompressorRecipe recipe) {
        recipe = AvaritiaRecipeManager.COMPRESSOR_RECIPES.remove(recipe.getRegistryName());
        if (recipe != null) {
            addBackup(recipe);
        }
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

    public void add(ICompressorRecipe recipe) {
        AvaritiaRecipeManager.COMPRESSOR_RECIPES.put(recipe.getRegistryName(), recipe);
        addScripted(recipe);
    }

    public void add(ItemStack output, IIngredient input, int cost) {
        if (GroovyLog.msg("Error adding avaritia compressor recipe")
                .add(IngredientHelper.isEmpty(output), () -> "output must not be empty")
                .add(IngredientHelper.isEmpty(input), () -> "input must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        if (cost <= 0) cost = 1;
        CompressorRecipe recipe = new CompressorRecipe(output, cost, true, Collections.singletonList(input.toMcIngredient()));
        recipe.setRegistryName(new ResourceLocation(GroovyScript.getRunConfig().getPackId()));
        add(recipe);
    }
}
