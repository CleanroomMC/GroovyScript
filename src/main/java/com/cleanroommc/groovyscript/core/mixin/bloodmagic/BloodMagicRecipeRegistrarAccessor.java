package com.cleanroommc.groovyscript.core.mixin.bloodmagic;

import WayofTime.bloodmagic.api.impl.BloodMagicRecipeRegistrar;
import WayofTime.bloodmagic.api.impl.recipe.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(value = BloodMagicRecipeRegistrar.class, remap = false)
public interface BloodMagicRecipeRegistrarAccessor {

    @Accessor
    Set<RecipeBloodAltar> getAltarRecipes();

    @Accessor
    Set<RecipeAlchemyTable> getAlchemyRecipes();

    @Accessor
    Set<RecipeTartaricForge> getTartaricForgeRecipes();

    @Accessor
    Set<RecipeAlchemyArray> getAlchemyArrayRecipes();

    @Accessor
    Set<RecipeSacrificeCraft> getSacrificeCraftRecipes();

}
