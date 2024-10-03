package com.cleanroommc.groovyscript.helper.ingredient;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.google.common.collect.Iterators;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ItemsIngredient extends IngredientBase implements Iterable<ItemStack> {

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
        ItemsIngredient ingredient = new ItemsIngredient(this.itemStacks);
        ingredient.amount = this.amount;
        ingredient.transform(transformer);
        ingredient.when(matchCondition);
        return ingredient;
    }

    @Override
    public Ingredient toMcIngredient() {
        return Ingredient.fromStacks(getMatchingStacks());
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

    public List<ItemStack> getItemStacks() {
        return Collections.unmodifiableList(this.itemStacks);
    }

    @NotNull
    @Override
    public Iterator<ItemStack> iterator() {
        return Iterators.unmodifiableIterator(this.itemStacks.iterator());
    }

}
