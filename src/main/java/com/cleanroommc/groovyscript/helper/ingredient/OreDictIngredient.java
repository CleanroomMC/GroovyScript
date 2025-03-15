package com.cleanroommc.groovyscript.helper.ingredient;

import com.cleanroommc.groovyscript.api.IOreDicts;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

public class OreDictIngredient extends ItemsIngredient implements Iterable<ItemStack>, IOreDicts {

    private final String oreDict;

    public OreDictIngredient(String oreDict) {
        super(OreDictionary.getOres(oreDict));
        this.oreDict = oreDict;
    }

    // fast copy
    private OreDictIngredient(String oreDict, List<ItemStack> itemStacks) {
        super(itemStacks);
        this.oreDict = oreDict;
    }

    public String getOreDict() {
        return oreDict;
    }

    @Override
    public List<String> getOreDicts() {
        return ImmutableList.of(getOreDict());
    }

    @Override
    public OreDictIngredient exactCopy() {
        OreDictIngredient oreDictIngredient = new OreDictIngredient(this.oreDict, getItemStacks());
        oreDictIngredient.setAmount(getAmount());
        oreDictIngredient.transformer = transformer;
        oreDictIngredient.matchCondition = matchCondition;
        return oreDictIngredient;
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
