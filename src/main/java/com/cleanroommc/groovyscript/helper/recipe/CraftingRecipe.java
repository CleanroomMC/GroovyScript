package com.cleanroommc.groovyscript.helper.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class CraftingRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    protected final net.minecraft.item.ItemStack output;
    protected final List<IIngredient> input;
    private final NonNullList<Ingredient> ingredients;

    public CraftingRecipe(ItemStack output, List<IIngredient> input) {
        this.output = output;
        this.input = input;
        this.ingredients = NonNullList.create();
        for (IIngredient ingredient : input) {
            this.ingredients.add(ingredient == null ? Ingredient.EMPTY : ingredient.toMcIngredient());
        }
    }

    @Override
    public @NotNull ItemStack getCraftingResult(@NotNull InventoryCrafting inv) {
        return output.copy();
    }

    @Override
    public @NotNull ItemStack getRecipeOutput() {
        return output;
    }

    public boolean matches(@Nullable IIngredient expectedInput, ItemStack givenInput) {
        return expectedInput == null || expectedInput.test(givenInput);
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }
}
