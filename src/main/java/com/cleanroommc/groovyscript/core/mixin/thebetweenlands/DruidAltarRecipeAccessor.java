package com.cleanroommc.groovyscript.core.mixin.thebetweenlands;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import thebetweenlands.api.recipes.IDruidAltarRecipe;
import thebetweenlands.common.recipe.misc.DruidAltarRecipe;

import java.util.ArrayList;

@Mixin(value = DruidAltarRecipe.class, remap = false)
public interface DruidAltarRecipeAccessor {

    @Accessor("druidAltarRecipes")
    static ArrayList<IDruidAltarRecipe> getRecipes() {
        throw new UnsupportedOperationException();
    }
}
