package com.cleanroommc.groovyscript.compat;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;
import net.minecraftforge.common.crafting.IShapedRecipe;

/**
 * Why does jei not a simple shaped wrapper like this?
 */
public class ShapedRecipeWrapper extends ShapelessRecipeWrapper<IShapedRecipe> implements IShapedCraftingRecipeWrapper {

    public ShapedRecipeWrapper(IJeiHelpers jeiHelpers, IShapedRecipe recipe) {
        super(jeiHelpers, recipe);
    }

    @Override
    public int getWidth() {
        return recipe.getRecipeWidth();
    }

    @Override
    public int getHeight() {
        return recipe.getRecipeHeight();
    }
}
