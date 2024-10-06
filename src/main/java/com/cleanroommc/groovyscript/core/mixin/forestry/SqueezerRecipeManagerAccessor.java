package com.cleanroommc.groovyscript.core.mixin.forestry;

import forestry.api.recipes.ISqueezerRecipe;
import forestry.factory.recipes.SqueezerRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(value = SqueezerRecipeManager.class, remap = false)
public interface SqueezerRecipeManagerAccessor {

    @Accessor
    static Set<ISqueezerRecipe> getRecipes() {
        throw new AssertionError();
    }
}
