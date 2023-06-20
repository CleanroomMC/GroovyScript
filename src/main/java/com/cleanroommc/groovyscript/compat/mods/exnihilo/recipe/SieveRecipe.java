package com.cleanroommc.groovyscript.compat.mods.exnihilo.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

public class SieveRecipe {
    public final Ingredient input;
    public final ItemStack output;
    public final float chance;
    public final int meshlevel;

    public SieveRecipe(Ingredient input, ItemStack output, float chance, int meshlevel) {
        this.input = input;
        this.output = output;
        this.chance = chance;
        this.meshlevel = meshlevel;
    }
}
