package com.cleanroommc.groovyscript.core.mixin.forestry;

import forestry.api.recipes.IMoistenerRecipe;
import forestry.factory.recipes.MoistenerRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(value = MoistenerRecipeManager.class, remap = false)
public interface MoistenerRecipeManagerAccessor {

    @Accessor
    static Set<IMoistenerRecipe> getRecipes() {
        throw new AssertionError();
    }
}
