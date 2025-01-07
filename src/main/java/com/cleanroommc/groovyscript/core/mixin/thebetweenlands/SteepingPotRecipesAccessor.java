package com.cleanroommc.groovyscript.core.mixin.thebetweenlands;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import thebetweenlands.common.recipe.misc.SteepingPotRecipes;

import java.util.List;

@Mixin(value = SteepingPotRecipes.class, remap = false)
public interface SteepingPotRecipesAccessor {

    @Accessor("recipes")
    static List<SteepingPotRecipes> getRecipes() {
        throw new UnsupportedOperationException();
    }
}
