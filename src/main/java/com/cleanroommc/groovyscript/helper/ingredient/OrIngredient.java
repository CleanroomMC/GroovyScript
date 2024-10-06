package com.cleanroommc.groovyscript.helper.ingredient;

import com.cleanroommc.groovyscript.api.IIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrIngredient extends IngredientBase {

    private final List<IIngredient> ingredients = new ArrayList<>();
    private int amount = 1;

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public IIngredient exactCopy() {
        OrIngredient orIngredient = new OrIngredient();
        orIngredient.ingredients.addAll(this.ingredients);
        orIngredient.setAmount(getAmount());
        orIngredient.transformer = transformer;
        orIngredient.matchCondition = matchCondition;
        return orIngredient;
    }

    @Override
    public Ingredient toMcIngredient() {
        return Ingredient.fromStacks(getMatchingStacks());
    }

    @Override
    public ItemStack[] getMatchingStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        for (IIngredient ingredient : this.ingredients) {
            Collections.addAll(stacks, ingredient.getMatchingStacks());
        }
        return stacks.toArray(new ItemStack[0]);
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        for (IIngredient ingredient : this.ingredients) {
            if (ingredient.test(itemStack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IIngredient or(IIngredient ingredient) {
        addIngredient(ingredient);
        return this;
    }

    public void addIngredient(IIngredient ingredient) {
        this.ingredients.add(ingredient);
    }
}
