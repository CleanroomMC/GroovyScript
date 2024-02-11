package com.cleanroommc.groovyscript.helper.ingredient;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.Iterators;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public class OreDictIngredient extends IngredientBase implements Iterable<ItemStack> {

    private final String oreDict;
    private int count = 1;

    public OreDictIngredient(String oreDict) {
        this.oreDict = oreDict;
    }

    public String getOreDict() {
        return oreDict;
    }

    @Override
    public int getAmount() {
        return count;
    }

    @Override
    public void setAmount(int amount) {
        count = Math.max(0, amount);
    }

    @Override
    public OreDictIngredient exactCopy() {
        OreDictIngredient oreDictIngredient = new OreDictIngredient(this.oreDict);
        oreDictIngredient.setAmount(this.count);
        oreDictIngredient.transformer = transformer;
        oreDictIngredient.matchCondition = matchCondition;
        return oreDictIngredient;
    }

    @Override
    public boolean matches(ItemStack stack) {
        // TODO this sucks
        if (IngredientHelper.isEmpty(stack)) return false;
        for (int id : OreDictionary.getOreIDs(stack)) {
            String oreName = OreDictionary.getOreName(id);
            if (oreDict.equals(oreName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Ingredient toMcIngredient() {
        return Ingredient.fromStacks(getMatchingStacks());
    }

    @GroovyBlacklist
    private List<ItemStack> prepareItemStacks() {
        List<ItemStack> stacks = OreDictionary.getOres(this.oreDict);
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i).copy();
            stack.setCount(getAmount());
            stacks.set(i, stack);
        }
        return stacks;
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        return prepareItemStacks().toArray(new ItemStack[0]);
    }

    public ItemStack getFirst() {
        return prepareItemStacks().get(0);
    }

    public ItemStack getAt(int index) {
        return prepareItemStacks().get(index);
    }

    @Override
    public String toString() {
        return "OreDictIngredient{ " + oreDict + " } * " + count;
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

    @NotNull
    @Override
    public Iterator<ItemStack> iterator() {
        return Iterators.unmodifiableIterator(prepareItemStacks().listIterator());
    }
}
