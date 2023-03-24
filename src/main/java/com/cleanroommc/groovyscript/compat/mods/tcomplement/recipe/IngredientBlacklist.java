package com.cleanroommc.groovyscript.compat.mods.tcomplement.recipe;

import com.cleanroommc.groovyscript.api.IIngredient;
import knightminer.tcomplement.library.IBlacklist;
import net.minecraft.item.ItemStack;

public class IngredientBlacklist implements IBlacklist {

    protected final IIngredient ingredient;

    public IngredientBlacklist(IIngredient ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public boolean matches(ItemStack itemStack) {
        return ingredient.test(itemStack);
    }
}
