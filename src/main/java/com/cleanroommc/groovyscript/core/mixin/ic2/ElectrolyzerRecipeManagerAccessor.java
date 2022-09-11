package com.cleanroommc.groovyscript.core.mixin.ic2;

import ic2.api.recipe.IElectrolyzerRecipeManager;
import ic2.core.recipe.ElectrolyzerRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ElectrolyzerRecipeManager.class)
public interface ElectrolyzerRecipeManagerAccessor {
    @Accessor
    Map<String, IElectrolyzerRecipeManager.ElectrolyzerRecipe> getFluidMap();
}
