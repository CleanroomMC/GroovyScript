package com.cleanroommc.groovyscript.api.wrapper;

import net.minecraft.item.ItemStack;

public class Ingredient {

    private final net.minecraft.item.crafting.Ingredient internal;


    public Ingredient(net.minecraft.item.crafting.Ingredient internal) {
        this.internal = internal;
    }

    public net.minecraft.item.crafting.Ingredient getMCIngredient() {
        return internal;
    }

    public ItemStack[] getMatchingStacks() {
        return internal.getMatchingStacks();
    }
}
