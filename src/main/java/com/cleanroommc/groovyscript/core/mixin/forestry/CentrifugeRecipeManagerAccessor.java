package com.cleanroommc.groovyscript.core.mixin.forestry;

import forestry.api.recipes.ICentrifugeRecipe;
import forestry.factory.recipes.CentrifugeRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(value = CentrifugeRecipeManager.class, remap = false)
public interface CentrifugeRecipeManagerAccessor {

    @Accessor
    static Set<ICentrifugeRecipe> getRecipes() {
        throw new AssertionError();
    }
}
