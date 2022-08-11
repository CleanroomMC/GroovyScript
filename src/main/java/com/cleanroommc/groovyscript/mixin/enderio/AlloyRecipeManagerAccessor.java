package com.cleanroommc.groovyscript.mixin.enderio;

import crazypants.enderio.base.recipe.IManyToOneRecipe;
import crazypants.enderio.base.recipe.alloysmelter.AlloyRecipeManager;
import crazypants.enderio.base.recipe.lookup.TriItemLookup;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = AlloyRecipeManager.class, remap = false)
public interface AlloyRecipeManagerAccessor {

    @Accessor
    TriItemLookup<IManyToOneRecipe> getLookup();

    @Accessor
    void setLookup(TriItemLookup<IManyToOneRecipe> lookup);

    @Invoker
    static void invokeAddRecipeToLookup(@NotNull TriItemLookup<IManyToOneRecipe> lookup, @NotNull IManyToOneRecipe recipe) {
        throw new AssertionError();
    }

    @Invoker
    void invokeAddJEIIntegration(@NotNull IManyToOneRecipe recipe);

}
