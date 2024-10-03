package com.cleanroommc.groovyscript.core.mixin.ic2;

import ic2.core.recipe.ScrapboxRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = ScrapboxRecipeManager.class, remap = false)
public interface ScrapboxRecipeManagerAccessor {

    @Accessor
    List<Object> getDrops();

}
