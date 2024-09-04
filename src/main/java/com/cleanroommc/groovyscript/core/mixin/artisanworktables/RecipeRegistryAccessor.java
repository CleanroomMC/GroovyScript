package com.cleanroommc.groovyscript.core.mixin.artisanworktables;

import com.codetaylor.mc.artisanworktables.api.internal.recipe.RecipeRegistry;
import com.codetaylor.mc.artisanworktables.api.recipe.IArtisanRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Map;

@Mixin(value = RecipeRegistry.class, remap = false)
public interface RecipeRegistryAccessor {
    @Accessor
    List<IArtisanRecipe> getRecipeList();

    @Accessor
    Map<String, IArtisanRecipe> getRecipeMap();
}
