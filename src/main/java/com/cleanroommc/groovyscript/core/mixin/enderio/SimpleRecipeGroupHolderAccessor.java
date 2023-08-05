package com.cleanroommc.groovyscript.core.mixin.enderio;

import crazypants.enderio.base.recipe.IMachineRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(targets = "crazypants.enderio.base.recipe.MachineRecipeRegistry$SimpleRecipeGroupHolder")
public interface SimpleRecipeGroupHolderAccessor {

    @Accessor
    Map<String, IMachineRecipe> getRecipes();
}
