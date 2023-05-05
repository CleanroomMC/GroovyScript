package com.cleanroommc.groovyscript.compat.mods.extendedcrafting;

import com.blakebr0.extendedcrafting.crafting.table.ITieredRecipe;
import com.blakebr0.extendedcrafting.crafting.table.TableRecipeManager;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;

import java.util.List;

public class TableCrafting extends VirtualizedRegistry<ITieredRecipe> {

    public TableRecipeBuilder.Shaped shapedBuilder() {
        return new TableRecipeBuilder.Shaped();
    }

    public TableRecipeBuilder.Shapeless shapelessBuilder() {
        return new TableRecipeBuilder.Shapeless();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> TableRecipeManager.getInstance().getRecipes().removeIf(r -> r == recipe));
        TableRecipeManager.getInstance().getRecipes().addAll(restoreFromBackup());
    }

    public ITieredRecipe addShaped(ItemStack output, List<List<IIngredient>> input) {
        return addShaped(0, output, input);
    }

    public ITieredRecipe addShaped(int tier, ItemStack output, List<List<IIngredient>> input) {
        return (ITieredRecipe) shapedBuilder()
                .matrix(input)
                .tier(tier)
                .output(output)
                .register();
    }

    public ITieredRecipe addShapeless(ItemStack output, List<List<IIngredient>> input) {
        return addShaped(0, output, input);
    }

    public ITieredRecipe addShapeless(int tier, ItemStack output, List<IIngredient> input) {
        return (ITieredRecipe) shapelessBuilder()
                .input(input)
                .tier(tier)
                .output(output)
                .register();
    }

    public ITieredRecipe add(ITieredRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            TableRecipeManager.getInstance().getRecipes().add(recipe);
        }
        return recipe;
    }

    public boolean removeByOutput(ItemStack stack) {
        return TableRecipeManager.getInstance().getRecipes().removeIf(recipe -> {
            if (recipe != null && recipe.getRecipeOutput().isItemEqual(stack)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    public boolean remove(ITieredRecipe recipe) {
        if (TableRecipeManager.getInstance().getRecipes().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public SimpleObjectStream<ITieredRecipe> streamRecipes() {
        return new SimpleObjectStream<>(TableRecipeManager.getInstance().getRecipes()).setRemover(this::remove);
    }

    public void removeAll() {
        TableRecipeManager.getInstance().getRecipes().forEach(this::addBackup);
        TableRecipeManager.getInstance().getRecipes().clear();
    }
}
