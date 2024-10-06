package com.cleanroommc.groovyscript.compat.vanilla;

import net.minecraft.item.ItemStack;

public interface ItemStackTransformer {

    ItemStack transform(ItemStack self);
}
