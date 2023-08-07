package com.cleanroommc.groovyscript.compat.mods.extendedcrafting;

import com.blakebr0.extendedcrafting.config.ModConfig;
import com.blakebr0.extendedcrafting.crafting.endercrafter.EnderCrafterRecipeManager;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;

public class EnderCrafting extends VirtualizedRegistry<IRecipe> {

    public EnderCrafting() {
        super();
    }

    public EnderRecipeBuilder.Shaped shapedBuilder() {
        return new EnderRecipeBuilder.Shaped();
    }

    public EnderRecipeBuilder.Shapeless shapelessBuilder() {
        return new EnderRecipeBuilder.Shapeless();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> EnderCrafterRecipeManager.getInstance().getRecipes().removeIf(r -> r == recipe));
        EnderCrafterRecipeManager.getInstance().getRecipes().addAll(restoreFromBackup());
    }

    public IRecipe addShaped(ItemStack output, List<List<IIngredient>> input) {
        return addShaped(ModConfig.confEnderTimeRequired, output, input);
    }

    public IRecipe addShaped(int time, ItemStack output, List<List<IIngredient>> input) {
        return (IRecipe) shapedBuilder()
                .matrix(input)
                .time(time)
                .output(output)
                .register();
    }

    public IRecipe addShapeless(ItemStack output, List<List<IIngredient>> input) {
        return addShaped(ModConfig.confEnderTimeRequired, output, input);
    }

    public IRecipe addShapeless(int time, ItemStack output, List<IIngredient> input) {
        return (IRecipe) shapelessBuilder()
                .input(input)
                .time(time)
                .output(output)
                .register();
    }

    public IRecipe add(IRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            EnderCrafterRecipeManager.getInstance().getRecipes().add(recipe);
        }
        return recipe;
    }

    public boolean removeByOutput(ItemStack stack) {
        return EnderCrafterRecipeManager.getInstance().getRecipes().removeIf(recipe -> {
            if (recipe != null && recipe.getRecipeOutput().isItemEqual(stack)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    public boolean remove(IRecipe recipe) {
        if (EnderCrafterRecipeManager.getInstance().getRecipes().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public SimpleObjectStream<IRecipe> streamRecipes() {
        return new SimpleObjectStream<>(EnderCrafterRecipeManager.getInstance().getRecipes()).setRemover(this::remove);
    }

    public void removeAll() {
        EnderCrafterRecipeManager.getInstance().getRecipes().forEach(this::addBackup);
        EnderCrafterRecipeManager.getInstance().getRecipes().clear();
    }
}
