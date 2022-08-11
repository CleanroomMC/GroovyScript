package com.cleanroommc.groovyscript.mixin.enderio;

import com.enderio.core.common.util.NNList;
import crazypants.enderio.base.recipe.Recipe;
import crazypants.enderio.base.recipe.sagmill.SagMillRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SagMillRecipeManager.class)
public interface SagMillRecipeManagerAccessor {

    @Accessor
    NNList<Recipe> getRecipes();

}
