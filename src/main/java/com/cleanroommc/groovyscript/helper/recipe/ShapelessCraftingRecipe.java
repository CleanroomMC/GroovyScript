package com.cleanroommc.groovyscript.helper.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShapelessCraftingRecipe extends CraftingRecipe {

    public ShapelessCraftingRecipe(ItemStack output, List<IIngredient> input) {
        super(output, input);
    }

    @Override
    public @NotNull MatchList getMatchingList(InventoryCrafting inv) {
        MatchList matches = new MatchList();

        List<Pair<ItemStack, Integer>> givenInputs = new ArrayList<>();
        // collect all items from the crafting matrix
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                givenInputs.add(Pair.of(itemstack, i));
            }
        }
        // check if expected and given inputs have the same count
        if (givenInputs.isEmpty() || givenInputs.size() != input.size()) return MatchList.EMPTY;
        List<IIngredient> input = new ArrayList<>(this.input);
        // go through each expected input and try to match it to an given input
        Iterator<IIngredient> ingredientIterator = input.iterator();
        main:
        while (ingredientIterator.hasNext()) {
            IIngredient ingredient = ingredientIterator.next();

            Iterator<Pair<ItemStack, Integer>> pairIterator = givenInputs.iterator();
            while (pairIterator.hasNext()) {
                Pair<ItemStack, Integer> pair = pairIterator.next();
                if (matches(ingredient, pair.getLeft())) {
                    // expected input matches given input so both get removed so they dont get checked again
                    matches.addMatch(ingredient, pair.getLeft(), pair.getRight());
                    ingredientIterator.remove();
                    pairIterator.remove();
                    if (givenInputs.isEmpty()) break main;
                    // skip to next expected ingredient
                    continue main;
                }
            }
            // at this point no given input could be matched for this expected input so return false
            return MatchList.EMPTY;
        }
        return input.isEmpty() ? matches : MatchList.EMPTY;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= input.size();
    }
}
