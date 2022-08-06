package com.cleanroommc.groovyscript.mixin.enderio;

import com.cleanroommc.groovyscript.registry.IReloadableRegistry;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.enderio.core.common.util.NNList;
import crazypants.enderio.base.Log;
import crazypants.enderio.base.recipe.IRecipe;
import crazypants.enderio.base.recipe.vat.VatRecipe;
import crazypants.enderio.base.recipe.vat.VatRecipeManager;
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

@Mixin(value = VatRecipeManager.class, remap = false)
public class VatRecipeManagerMixin implements IReloadableRegistry<IRecipe> {

    @Shadow
    @Final
    @Nonnull
    private NNList<IRecipe> recipes;

    @Unique
    @Final
    private List<IRecipe> backupRecipes = new ArrayList<>();

    @Override
    public void onReload() {
        recipes.clear();
        recipes.addAll(backupRecipes);
    }

    @Override
    public void removeEntry(IRecipe iRecipe) {
        recipes.removeIf(recipe -> recipe == iRecipe);
    }

    @Inject(method = "addRecipe", at = @At("HEAD"), cancellable = true)
    public void addRecipe(IRecipe recipe, CallbackInfo ci) {
        if (!recipe.isValid()) {
            Log.debug("Could not add invalid Vat recipe: " + recipe);
        } else {
            VatRecipe vatRecipe = new VatRecipe(recipe);
            this.recipes.add(vatRecipe);
            if (!ReloadableRegistryManager.isShouldRegisterAsReloadable()) {
                backupRecipes.add(vatRecipe);
            }
        }
        ci.cancel();
    }
}
