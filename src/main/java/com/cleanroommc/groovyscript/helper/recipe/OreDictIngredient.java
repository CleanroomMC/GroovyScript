package com.cleanroommc.groovyscript.helper.recipe;

import com.cleanroommc.groovyscript.api.IIngredient;
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
        return OreDictionary.getOres(oreDict).toArray(new ItemStack[0]);
    }

    @Override
    public String toString() {
        return "ore:" + oreDict + " * " + count;
    }
}
