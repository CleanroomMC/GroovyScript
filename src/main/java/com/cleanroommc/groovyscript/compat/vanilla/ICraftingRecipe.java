package com.cleanroommc.groovyscript.compat.vanilla;

import groovy.lang.Closure;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ICraftingRecipe {

    @Nullable
    Closure<Void> getRecipeAction();

    @Nullable
    Closure<ItemStack> getRecipeFunction();
}
