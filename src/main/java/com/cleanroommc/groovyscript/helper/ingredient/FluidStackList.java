package com.cleanroommc.groovyscript.helper.ingredient;

import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collection;

public class FluidStackList extends ArrayList<FluidStack> {

    public FluidStackList() {}

    public FluidStackList(Collection<FluidStack> collection) {
        super(collection);
    }

    public FluidStack getOrEmpty(int i) {
        if (i < 0 || i >= size()) {
            return null;
        }
        return get(i);
    }

    public int getRealSize() {
        if (isEmpty()) return 0;
        int realSize = 0;
        for (FluidStack t : this) {
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
            FluidStack itemStack = get(i);
            if (!IngredientHelper.isEmpty(itemStack)) {
                set(i, itemStack.copy());
            }
        }
    }
}
