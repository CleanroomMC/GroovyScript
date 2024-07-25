package com.cleanroommc.groovyscript.core.mixin.jei;

import mezz.jei.gui.recipes.IRecipeGuiLogic;
import mezz.jei.gui.recipes.RecipeLayout;
import mezz.jei.gui.recipes.RecipesGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = RecipesGui.class, remap = false)
public interface RecipesGuiAccessor {

    @Accessor("recipeLayouts")
    List<RecipeLayout> getRecipeLayouts();

    @Accessor("logic")
    IRecipeGuiLogic getLogic();

}
