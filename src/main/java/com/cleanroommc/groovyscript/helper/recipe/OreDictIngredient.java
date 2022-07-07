package com.cleanroommc.groovyscript.helper.recipe;

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

    @Override
    public OreDictIngredient setCount(int amount) {
        count = amount;
        return this;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean test(ItemStack stack) {
        // TODO this sucks
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
        OreDictIngredient ingredient = new OreDictIngredient(oreDict);
        ingredient.setCount(count);
        return ingredient;
    }

    @Override
    public Ingredient toMcIngredient() {
        return Ingredient.fromStacks(OreDictionary.getOres(oreDict).toArray(new ItemStack[0]));
    }

    @Override
    public String toString() {
        return "ore:" + oreDict + " * " + count;
    }
}
