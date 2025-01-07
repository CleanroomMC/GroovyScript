package com.cleanroommc.groovyscript.core.mixin.thebetweenlands;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import thebetweenlands.api.recipes.ICrabPotFilterRecipeSilt;
import thebetweenlands.common.recipe.misc.CrabPotFilterRecipeSilt;

import java.util.List;

@Mixin(value = CrabPotFilterRecipeSilt.class, remap = false)
public interface CrabPotFilterRecipeSiltAccessor {

    @Accessor("RECIPES")
    static List<ICrabPotFilterRecipeSilt> getRecipes() {
        throw new UnsupportedOperationException();
    }
}
