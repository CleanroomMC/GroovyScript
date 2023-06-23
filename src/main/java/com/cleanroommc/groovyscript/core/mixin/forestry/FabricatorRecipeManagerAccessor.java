package com.cleanroommc.groovyscript.core.mixin.forestry;

import forestry.api.recipes.IFabricatorRecipe;
import forestry.factory.recipes.FabricatorRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(value = FabricatorRecipeManager.class, remap = false)
public interface FabricatorRecipeManagerAccessor {

    @Accessor
    static Set<IFabricatorRecipe> getRecipes() {
        throw new AssertionError();
    }
}
