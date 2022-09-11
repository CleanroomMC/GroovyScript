package com.cleanroommc.groovyscript.compat.mods.ic2;

import com.cleanroommc.groovyscript.api.IIngredient;
import ic2.api.recipe.IRecipeInput;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.List;

public class RecipeInput implements IRecipeInput {
    private final IIngredient ingredient;

    public RecipeInput(IIngredient ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        return this.ingredient.test(itemStack);
    }

    @Override
    public int getAmount() {
        return this.ingredient.getAmount();
    }

    @Override
    public List<ItemStack> getInputs() {
        return Arrays.asList(this.ingredient.getMatchingStacks());
    }
}
