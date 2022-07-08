package com.cleanroommc.groovyscript.helper.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShapelessCraftingRecipe extends CraftingRecipe {

    public ShapelessCraftingRecipe(ItemStack output, List<IIngredient> input) {
        super(output, input);
    }

    @Override
    public boolean matches(InventoryCrafting inv, @NotNull World worldIn) {
        List<ItemStack> givenInputs = new ArrayList<>();
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                givenInputs.add(itemstack);
            }
        }
        if (givenInputs.isEmpty() || givenInputs.size() != input.size()) return false;
        List<IIngredient> input = new ArrayList<>(this.input);
        Iterator<IIngredient> ingredientIterator = input.iterator();
        while (ingredientIterator.hasNext()) {
            IIngredient ingredient = ingredientIterator.next();

            Iterator<ItemStack> itemStackIterator = givenInputs.iterator();
            while (itemStackIterator.hasNext()) {
                ItemStack itemStack = itemStackIterator.next();
                if (matches(ingredient, itemStack)) {
                    ingredientIterator.remove();
                    itemStackIterator.remove();
                }
            }
            if (givenInputs.isEmpty()) break;
        }
        return input.isEmpty();
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= input.size();
    }
}
