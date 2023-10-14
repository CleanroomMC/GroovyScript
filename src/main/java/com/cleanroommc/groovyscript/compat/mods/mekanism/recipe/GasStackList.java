package com.cleanroommc.groovyscript.compat.mods.mekanism.recipe;

import com.cleanroommc.groovyscript.compat.mods.mekanism.Mekanism;
import mekanism.api.gas.GasStack;

import java.util.ArrayList;
import java.util.Collection;

public class GasStackList extends ArrayList<GasStack> {

    public GasStackList() {
    }

    public GasStackList(Collection<GasStack> collection) {
        super(collection);
    }

    public GasStack getOrEmpty(int i) {
        if (i < 0 || i >= size()) {
            return null;
        }
        return get(i);
    }

    public int getRealSize() {
        if (isEmpty()) return 0;
        int realSize = 0;
        for (GasStack t : this) {
            if (!Mekanism.isEmpty(t)) realSize++;
        }
        return realSize;
    }

    public void trim() {
        if (!isEmpty()) {
            removeIf(Mekanism::isEmpty);
        }
    }

    public void copyElements() {
        for (int i = 0; i < size(); i++) {
            GasStack gasStack = get(i);
            if (!Mekanism.isEmpty(gasStack)) {
                set(i, gasStack.copy());
            }
        }
    }

}
