package com.cleanroommc.groovyscript.helper.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;

public class ItemStack implements IIngredient {

    public static ItemStack parse(String raw) {
        String[] parts = raw.split(":");
        if (parts.length < 2) throw new IllegalArgumentException();
        Item item1 = ForgeRegistries.ITEMS.getValue(new ResourceLocation(parts[0], parts[1]));
        if (item1 == null) {
            throw new NoSuchElementException("Can't find item for " + raw);
        }
        int meta = 0;
        if (parts.length > 2) {
            try {
                meta = Integer.parseInt(parts[2]);
            } catch (NumberFormatException ignored) {
            }
        }
        return new ItemStack(new net.minecraft.item.ItemStack(item1, 1, meta), false);
    }

    private final net.minecraft.item.ItemStack internal;

    private ItemStack(net.minecraft.item.ItemStack internal, boolean makeCopy) {
        this.internal = makeCopy ? internal.copy() : internal;
    }

    public ItemStack(net.minecraft.item.ItemStack internal) {
        this(internal, true);
    }

    public Item getItem() {
        return internal.getItem();
    }

    public int getMetadata() {
        return internal.getMetadata();
    }

    @Nullable
    public NBTTagCompound getNbt() {
        return internal.getTagCompound();
    }

    public boolean hasNbt() {
        return internal.hasTagCompound();
    }

    public String getRegistryName() {
        return internal.getItem().getRegistryName().toString();
    }

    @Override
    public ItemStack setCount(int amount) {
        internal.setCount(amount);
        return this;
    }

    @Override
    public int getCount() {
        return internal.getCount();
    }

    @Override
    public boolean test(net.minecraft.item.ItemStack stack) {
        return OreDictionary.itemMatches(internal, stack, false);
    }

    public net.minecraft.item.ItemStack getInternal() {
        return internal;
    }

    public net.minecraft.item.ItemStack createMcItemStack() {
        return internal.copy();
    }

    @Override
    public ItemStack exactCopy() {
        return new ItemStack(internal);
    }

    @Override
    public Ingredient toMcIngredient() {
        return Ingredient.fromStacks(internal);
    }

    @Override
    public String toString() {
        return internal.toString();
    }
}
