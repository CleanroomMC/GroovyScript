package com.cleanroommc.groovyscript.helper.ingredient;

import com.cleanroommc.groovyscript.api.IOreDicts;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class OreDictIngredient extends IngredientBase implements Iterable<ItemStack>, IOreDicts {

    private final String oreDict;
    private int amount = 1;

    public OreDictIngredient(String oreDict) {
        this.oreDict = oreDict;
    }

    private static ItemStack selectItemStack(List<ItemStack> stacks, int index, int amount) {
        if (amount == 0 || stacks.isEmpty() || stacks.size() < index) return ItemStack.EMPTY;
        ItemStack stack = stacks.get(index).copy();
        stack.setCount(amount);
        return stack;
    }

    public String getOreDict() {
        return oreDict;
    }

    @Override
    public @UnmodifiableView Collection<String> getOreDicts() {
        return ImmutableList.of(getOreDict());
    }

    @Override
    public OreDictIngredient exactCopy() {
        OreDictIngredient oreDictIngredient = new OreDictIngredient(this.oreDict);
        oreDictIngredient.amount = amount;
        oreDictIngredient.transformer = transformer;
        oreDictIngredient.matchCondition = matchCondition;
        return oreDictIngredient;
    }

    @Override
    public Ingredient toMcIngredient() {
        return Ingredient.fromStacks(getMatchingStacks());
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        var stacks = getItemStacks();
        ItemStack[] output = new ItemStack[stacks.size()];
        for (int i = 0; i < output.length; i++) {
            output[i] = selectItemStack(stacks, i, amount);
        }
        return output;
    }

    @Override
    public ItemStack getAt(int index) {
        return selectItemStack(getItemStacks(), index, amount);
    }

    @Override
    public int getAmount() {
        return getItemStacks().isEmpty() ? 0 : amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = Math.max(0, amount);
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        for (ItemStack itemStack1 : getItemStacks()) {
            if (OreDictionary.itemMatches(itemStack1, itemStack, false)) {
                return true;
            }
        }
        return false;
    }

    private List<ItemStack> getItemStacks() {
        return OreDictionary.getOres(oreDict);
    }

    @Override
    public @NotNull Iterator<ItemStack> iterator() {
        return new AbstractIterator<>() {

            private int index = 0;

            @Override
            protected ItemStack computeNext() {
                var stacks = getItemStacks();
                if (index >= stacks.size()) return endOfData();
                return selectItemStack(stacks, index++, amount);
            }
        };
    }

    @Override
    public String toString() {
        return "OreDictIngredient{ " + oreDict + " } * " + getAmount();
    }

    public void add(ItemStack itemStack) {
        VanillaModule.oreDict.add(this.oreDict, itemStack);
    }

    public void add(ItemStack... itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            add(itemStack);
        }
    }

    public void add(Iterable<ItemStack> itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            add(itemStack);
        }
    }

    public void add(OreDictIngredient ingredient) {
        add(OreDictionary.getOres(ingredient.oreDict));
    }

    public void remove(ItemStack itemStack) {
        VanillaModule.oreDict.remove(this.oreDict, itemStack);
    }

    public void remove(ItemStack... itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            remove(itemStack);
        }
    }

    public void remove(Iterable<ItemStack> itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            remove(itemStack);
        }
    }
}
