package com.cleanroommc.groovyscript.core.mixin.woot;

import ipsis.woot.farming.SpawnRecipe;
import ipsis.woot.farming.SpawnRecipeRepository;
import ipsis.woot.util.WootMobName;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;

@Mixin(value = SpawnRecipeRepository.class, remap = false)
public interface SpawnRecipeRepositoryAccessor {

    @Accessor
    HashMap<WootMobName, SpawnRecipe> getRecipes();

    @Accessor
    SpawnRecipe getDefaultSpawnRecipe();
}
