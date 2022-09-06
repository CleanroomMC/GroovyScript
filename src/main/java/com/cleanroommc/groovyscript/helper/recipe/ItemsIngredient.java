package com.cleanroommc.groovyscript.helper.recipe;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.ItemStackList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Collection;
import java.util.Collections;

public class ItemsIngredient extends IngredientBase {

    private final ItemStackList itemStacks = new ItemStackList();
    private int amount = 1;

    public ItemsIngredient(ItemStack... itemStacks) {
        Collections.addAll(this.itemStacks, itemStacks);
        this.itemStacks.trim();
        this.itemStacks.copyElements();
    }

    public ItemsIngredient(Collection<ItemStack> itemStacks) {
        this.itemStacks.addAll(itemStacks);
        this.itemStacks.trim();
        this.itemStacks.copyElements();
    }

    @Override
    public IIngredient exactCopy() {
        return new ItemsIngredient(this.itemStacks);
    }

    @Override
    public Ingredient toMcIngredient() {
        return Ingredient.fromStacks(itemStacks.toArray(new ItemStack[0]));
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        ItemStack[] stacks = itemStacks.toArray(new ItemStack[0]);
        for (int i = 0; i < stacks.length; i++) {
            ItemStack stack = stacks[i].copy();
            stack.setCount(getAmount());
            stacks[i] = stack;
        }
        return stacks;
    }

    @Override
    public String asGroovyCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getAmount() {
        return itemStacks.isEmpty() ? 0 : amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = Math.max(0, amount);
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        for (ItemStack itemStack1 : itemStacks) {
            if (OreDictionary.itemMatches(itemStack1, itemStack, false)) {
                return true;
            }
        }
        return false;
    }
}
