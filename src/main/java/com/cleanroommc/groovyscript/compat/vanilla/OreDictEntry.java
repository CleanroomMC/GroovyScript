package com.cleanroommc.groovyscript.compat.vanilla;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import net.minecraft.item.ItemStack;

@SuppressWarnings("ClassCanBeRecord")
public class OreDictEntry {

    public final String name;
    public final ItemStack stack;

    @GroovyBlacklist
    public OreDictEntry(String name, ItemStack stack) {
        this.name = name;
        this.stack = stack.copy();
        this.stack.setTagCompound(null);
    }
}
