package com.cleanroommc.groovyscript.compat.mods.forestry.recipe;

import forestry.apiculture.genetics.alleles.AlleleBeeSpecies;
import net.minecraft.item.ItemStack;

@SuppressWarnings("ClassCanBeRecord")
public class BeeProduct {

    public final AlleleBeeSpecies species;
    public final ItemStack item;
    public final boolean special;
    public final float chance;

    public BeeProduct(AlleleBeeSpecies species, ItemStack item, float chance, boolean special) {
        this.species = species;
        this.item = item;
        this.chance = chance;
        this.special = special;
    }
}
