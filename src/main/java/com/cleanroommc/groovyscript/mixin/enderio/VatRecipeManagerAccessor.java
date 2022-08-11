package com.cleanroommc.groovyscript.mixin.enderio;

import com.enderio.core.common.util.NNList;
import crazypants.enderio.base.recipe.Recipe;
import crazypants.enderio.base.recipe.vat.VatRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VatRecipeManager.class)
public interface VatRecipeManagerAccessor {

    @Accessor
    NNList<Recipe> getRecipes();

}
