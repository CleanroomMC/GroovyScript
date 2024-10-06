package com.cleanroommc.groovyscript.core.mixin.forestry;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.factory.recipes.CarpenterRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(value = CarpenterRecipeManager.class, remap = false)
public interface CarpenterRecipeManagerAccessor {

    @Accessor
    static Set<ICarpenterRecipe> getRecipes() {
        throw new AssertionError();
    }
}
