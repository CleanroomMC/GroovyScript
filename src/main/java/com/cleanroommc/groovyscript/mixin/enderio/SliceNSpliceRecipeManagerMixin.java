package com.cleanroommc.groovyscript.mixin.enderio;

import com.cleanroommc.groovyscript.registry.IReloadableRegistry;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.enderio.core.common.util.NNList;
import crazypants.enderio.base.recipe.IManyToOneRecipe;
import crazypants.enderio.base.recipe.slicensplice.SliceAndSpliceRecipeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Mixin(value = SliceAndSpliceRecipeManager.class, remap = false)
public class SliceNSpliceRecipeManagerMixin implements IReloadableRegistry<IManyToOneRecipe> {

    @Shadow
    @Final
    @Nonnull
    private NNList<IManyToOneRecipe> recipes;
    @Unique
    @Final
    private List<IManyToOneRecipe> backupRecipes = new ArrayList<>();

    @Override
    public void onReload() {
        recipes.clear();
        recipes.addAll(backupRecipes);
    }

    @Override
    public void removeEntry(IManyToOneRecipe iRecipe) {
        backupRecipes.removeIf(r -> r == iRecipe);
    }

    @Inject(method = "addRecipeInternal", at = @At("HEAD"))
    public void addRecipe(IManyToOneRecipe recipe, CallbackInfo ci) {
        if (!ReloadableRegistryManager.isShouldRegisterAsReloadable()) {
            backupRecipes.add(recipe);
        }
    }
}
