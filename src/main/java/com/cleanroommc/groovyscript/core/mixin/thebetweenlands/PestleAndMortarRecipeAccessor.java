package com.cleanroommc.groovyscript.core.mixin.thebetweenlands;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import thebetweenlands.api.recipes.IPestleAndMortarRecipe;
import thebetweenlands.common.recipe.mortar.PestleAndMortarRecipe;

import java.util.List;

@Mixin(value = PestleAndMortarRecipe.class, remap = false)
public interface PestleAndMortarRecipeAccessor {

    @Accessor("recipes")
    static List<IPestleAndMortarRecipe> getRecipes() {
        throw new UnsupportedOperationException();
    }
}
