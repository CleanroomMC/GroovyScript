package com.cleanroommc.groovyscript.core.mixin.jei;

import mezz.jei.gui.ingredients.IngredientLookupState;
import mezz.jei.gui.recipes.RecipeGuiLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RecipeGuiLogic.class, remap = false)
public interface RecipeGuiLogicAccessor {

    @Accessor("state")
    IngredientLookupState getState();

}
