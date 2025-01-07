package com.cleanroommc.groovyscript.core.mixin.thebetweenlands;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import thebetweenlands.api.recipes.ISmokingRackRecipe;
import thebetweenlands.common.recipe.misc.SmokingRackRecipe;

import java.util.List;

@Mixin(value = SmokingRackRecipe.class, remap = false)
public interface SmokingRackRecipeAccessor {

    @Accessor("RECIPES")
    static List<ISmokingRackRecipe> getRecipes() {
        throw new UnsupportedOperationException();
    }
}
