package com.cleanroommc.groovyscript.helper;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;

/**
 * A item stack list with helpers to handle empty stacks
 */
public class ItemStackList extends ArrayList<ItemStack> {

    public int getRealSize() {
        if (isEmpty()) return 0;
        int realSize = 0;
        for (ItemStack t : this) {
            if (!IngredientHelper.isEmpty(t)) realSize++;
        }
        return realSize;
    }

    public void trim() {
        if (!isEmpty()) {
            removeIf(IngredientHelper::isEmpty);
        }
    }

    public void copyElements() {
        for (int i = 0; i < size(); i++) {
            ItemStack itemStack = get(i);
            if (!IngredientHelper.isEmpty(itemStack)) {
                set(i, itemStack.copy());
            }
        }
    }
}
