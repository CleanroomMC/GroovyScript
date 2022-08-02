package com.cleanroommc.groovyscript.helper.recipe;

import com.cleanroommc.groovyscript.api.IIngredient;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class CraftingRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    protected final net.minecraft.item.ItemStack output;
    protected final List<IIngredient> input;
    private final NonNullList<Ingredient> ingredients;

    public CraftingRecipe(ItemStack output, List<IIngredient> input) {
        this.output = output;
        this.input = input;
        this.ingredients = NonNullList.create();
        for (int i = 0; i < this.input.size(); i++) {
            if (this.input.get(i) == null) this.input.set(i, IIngredient.EMPTY);
        }
        for (IIngredient ingredient : this.input) {
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

    @Override
    public @NotNull NonNullList<ItemStack> getRemainingItems(@NotNull InventoryCrafting inv) {
        NonNullList<ItemStack> result = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (Triple<IIngredient, ItemStack, Integer> pair : getMatchingList(inv)) {
            ItemStack itemStack = pair.getLeft().applyTransform(pair.getMiddle().copy());
            result.set(pair.getRight(), itemStack == null ? ItemStack.EMPTY : itemStack);
        }
        return result;
    }

    @Override
    public boolean matches(@NotNull InventoryCrafting inv, @NotNull World worldIn) {
        return !getMatchingList(inv).isEmpty();
    }

    @NotNull
    public abstract MatchList getMatchingList(InventoryCrafting inv);

    public static class MatchList implements Iterable<Triple<IIngredient, ItemStack, Integer>> {
        public static final MatchList EMPTY = new MatchList() {
            @Override
            public void addMatch(IIngredient ingredient, ItemStack itemStack, int itemSlotIndex) {
                throw new UnsupportedOperationException();
            }
        };

        private final List<Triple<IIngredient, ItemStack, Integer>> matches = new ArrayList<>();

        public void addMatch(IIngredient ingredient, ItemStack itemStack, int itemSlotIndex) {
            matches.add(Triple.of(ingredient, itemStack, itemSlotIndex));
        }

        public boolean isEmpty() {
            return matches.isEmpty();
        }

        @NotNull
        @Override
        public Iterator<Triple<IIngredient, ItemStack, Integer>> iterator() {
            return matches.iterator();
        }
    }
}
