package com.cleanroommc.groovyscript.helper.recipe;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.IngredientHelper;
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
}
