package com.cleanroommc.groovyscript.compat.vanilla;

import net.minecraft.item.ItemStack;

public class OreDictEntry {

    public final String name;
    public final ItemStack stack;

    public OreDictEntry(String name, ItemStack stack) {
        if (name.isEmpty()) throw new IllegalArgumentException("OreDictEntry name must not be empty");
        this.name = name;
        if (stack.isEmpty()) throw new IllegalArgumentException("OreDictEntry stack must not be empty");
        this.stack = stack;
    }
}
