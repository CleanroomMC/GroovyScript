package com.cleanroommc.groovyscript.core.mixin.thebetweenlands;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import thebetweenlands.api.recipes.IPurifierRecipe;
import thebetweenlands.common.recipe.purifier.PurifierRecipe;

import java.util.List;

@Mixin(value = PurifierRecipe.class, remap = false)
public interface PurifierRecipeAccessor {

    @Accessor("RECIPES")
    static List<IPurifierRecipe> getRecipes() {
        throw new UnsupportedOperationException();
    }
}
