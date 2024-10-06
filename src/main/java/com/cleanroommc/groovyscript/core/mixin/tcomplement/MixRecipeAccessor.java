package com.cleanroommc.groovyscript.core.mixin.tcomplement;

import knightminer.tcomplement.library.steelworks.MixAdditive;
import knightminer.tcomplement.library.steelworks.MixRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import slimeknights.mantle.util.RecipeMatchRegistry;

import java.util.Map;

@Mixin(value = MixRecipe.class, remap = false)
public interface MixRecipeAccessor {

    @Accessor
    Map<MixAdditive, RecipeMatchRegistry> getAdditives();
}
