package com.cleanroommc.groovyscript.core.mixin.tcomplement;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import slimeknights.mantle.util.RecipeMatch;
import slimeknights.mantle.util.RecipeMatchRegistry;

import java.util.PriorityQueue;

@Mixin(value = RecipeMatchRegistry.class, remap = false)
public interface RecipeMatchRegistryAccessor {

    @Accessor
    PriorityQueue<RecipeMatch> getItems();
}
