package com.cleanroommc.groovyscript.compat.mods.avaritia;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import morph.avaritia.recipe.AvaritiaRecipeManager;
import morph.avaritia.recipe.extreme.IExtremeRecipe;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ExtremeCrafting extends VirtualizedRegistry<IExtremeRecipe> {

    public ExtremeRecipeBuilder.Shaped shapedBuilder() {
        return new ExtremeRecipeBuilder.Shaped();
    }

    public ExtremeRecipeBuilder.Shapeless shapelessBuilder() {
        return new ExtremeRecipeBuilder.Shapeless();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> AvaritiaRecipeManager.EXTREME_RECIPES.values().removeIf(r -> r == recipe));
        restoreFromBackup().forEach(recipe -> AvaritiaRecipeManager.EXTREME_RECIPES.put(recipe.getRegistryName(), recipe));
    }

    public IExtremeRecipe addShaped(ItemStack output, List<List<IIngredient>> input) {
        return (IExtremeRecipe) shapedBuilder()
                .matrix(input)
                .output(output)
                .register();
    }

    public IExtremeRecipe addShapeless(ItemStack output, List<IIngredient> input) {
        return (IExtremeRecipe) shapelessBuilder()
                .input(input)
                .output(output)
                .register();
    }

    public IExtremeRecipe add(IExtremeRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            AvaritiaRecipeManager.EXTREME_RECIPES.put(recipe.getRegistryName(), recipe);
        }
        return recipe;
    }

    public boolean removeByOutput(ItemStack stack) {
        return AvaritiaRecipeManager.EXTREME_RECIPES.values().removeIf(recipe -> {
            if (recipe != null && recipe.getRecipeOutput().isItemEqual(stack)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    public boolean remove(IExtremeRecipe recipe) {
        recipe = AvaritiaRecipeManager.EXTREME_RECIPES.remove(recipe.getRegistryName());
        if (recipe != null) {
            addBackup(recipe);
        }
        return recipe != null;
    }

    public SimpleObjectStream<IExtremeRecipe> streamRecipes() {
        return new SimpleObjectStream<>(AvaritiaRecipeManager.EXTREME_RECIPES.values()).setRemover(this::remove);
    }

    public void removeAll() {
        AvaritiaRecipeManager.EXTREME_RECIPES.values().forEach(this::addBackup);
        AvaritiaRecipeManager.EXTREME_RECIPES.values().clear();
    }
}
