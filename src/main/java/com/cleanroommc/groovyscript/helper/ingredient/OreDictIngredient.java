package com.cleanroommc.groovyscript.helper.ingredient;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictIngredient implements IIngredient {

    private final String oreDict;
    private int count = 1;

    // TODO wildcards like "ingot*"
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
    public boolean test(ItemStack stack) {
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
    public IIngredient exactCopy() {
        return (IIngredient) new OreDictIngredient(oreDict).withAmount(count);
    }

    @Override
    public Ingredient toMcIngredient() {
        return Ingredient.fromStacks(getMatchingStacks());
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        ItemStack[] stacks = OreDictionary.getOres(oreDict).toArray(new ItemStack[0]);
        for (int i = 0; i < stacks.length; i++) {
            ItemStack stack = stacks[i].copy();
            stack.setCount(getAmount());
            stacks[i] = stack;
        }
        return stacks;
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
}
