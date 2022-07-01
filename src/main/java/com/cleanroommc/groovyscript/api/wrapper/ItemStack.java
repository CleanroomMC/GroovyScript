package com.cleanroommc.groovyscript.api.wrapper;

import crafttweaker.api.item.IItemStack;
import crafttweaker.mc1120.item.MCItemStack;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Objects;

public class ItemStack extends Ingredient {

    private final net.minecraft.item.ItemStack internal;

    public ItemStack(net.minecraft.item.ItemStack internal) {
        super(net.minecraft.item.crafting.Ingredient.fromStacks(internal));
        this.internal = internal;
    }

    public net.minecraft.item.ItemStack getMCItemStack() {
        return internal;
    }

    public IItemStack getCTItemStack() {
        return new MCItemStack(getMCItemStack());
    }

    public int getCount() {
        return getMCItemStack().getCount();
    }

    public void setCount(int count) {
        getMCItemStack().setCount(count);
    }

    public NBTTagCompound getTag() {
        return getMCItemStack().getTagCompound();
    }

    public Item getItem() {
        return getMCItemStack().getItem();
    }

    public int getMetadata() {
        return getMCItemStack().getMetadata();
    }

    public boolean isEmpty() {
        return getMCItemStack().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemStack itemStack = (ItemStack) o;
        return internal.getMetadata() == itemStack.getMetadata() &&
                Objects.equals(itemStack.getItem(), internal.getItem()) &&
                Objects.equals(itemStack.getTag(), internal.getTagCompound());
    }

    @Override
    public int hashCode() {
        return Objects.hash(internal.getItem(), internal.getMetadata(), internal.getTagCompound());
    }
}
