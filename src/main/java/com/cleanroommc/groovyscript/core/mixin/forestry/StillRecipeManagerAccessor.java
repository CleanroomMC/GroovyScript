package com.cleanroommc.groovyscript.core.mixin.forestry;

import forestry.api.recipes.IStillRecipe;
import forestry.factory.recipes.StillRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(value = StillRecipeManager.class, remap = false)
public interface StillRecipeManagerAccessor {

    @Accessor
    static Set<IStillRecipe> getRecipes() {
        throw new AssertionError();
    }
}
