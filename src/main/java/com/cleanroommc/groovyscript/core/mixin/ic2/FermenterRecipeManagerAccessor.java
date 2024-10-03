package com.cleanroommc.groovyscript.core.mixin.ic2;

import ic2.api.recipe.IFermenterRecipeManager;
import ic2.core.recipe.FermenterRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = FermenterRecipeManager.class, remap = false)
public interface FermenterRecipeManagerAccessor {

    @Accessor
    Map<String, IFermenterRecipeManager.FermentationProperty> getFluidMap();

}
