package com.cleanroommc.groovyscript.core.mixin.jei;

import mezz.jei.api.recipe.IIngredientType;
import mezz.jei.plugins.jei.info.IngredientInfoRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = IngredientInfoRecipe.class, remap = false)
public interface IngredientInfoRecipeAccessor<T> {

    @Accessor
    List<T> getIngredients();

    @Accessor
    IIngredientType<T> getIngredientType();

}
