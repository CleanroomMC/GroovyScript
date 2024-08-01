package com.cleanroommc.groovyscript.core.mixin.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.gui.recipes.RecipeLayout;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RecipeLayout.class, remap = false)
public interface RecipeLayoutAccessor {

    @Accessor("recipeWrapper")
    IRecipeWrapper getRecipeWrapper();

}
