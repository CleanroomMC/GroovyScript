package com.cleanroommc.groovyscript.mixin.enderio;

import com.cleanroommc.groovyscript.registry.IReloadableRegistry;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import crazypants.enderio.base.recipe.IMachineRecipe;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nonnull;
import java.util.Map;

@Mixin(targets = "crazypants.enderio.base.recipe.MachineRecipeRegistry$SimpleRecipeGroupHolder", remap = false)
public class RecipeGroupHolderMixin implements IReloadableRegistry<IMachineRecipe> {

    @Shadow
    @Final
    @Nonnull
    private Map<String, IMachineRecipe> recipes;

    @Unique
    @Final
    private Map<String, IMachineRecipe> backupRecipes = new Object2ObjectOpenHashMap<>();

    @Override
    public void onReload() {
        recipes.clear();
        recipes.putAll(backupRecipes);
    }

    @Override
    public void removeEntry(IMachineRecipe iMachineRecipe) {
        recipes.remove(iMachineRecipe.getUid());
    }

    @Inject(method = "addRecipe", at = @At("HEAD"))
    public void addRecipe(IMachineRecipe recipe, CallbackInfo ci) {
        if (!ReloadableRegistryManager.isShouldRegisterAsReloadable()) {
            backupRecipes.put(recipe.getUid(), recipe);
        }
    }

    @Inject(method = "asSorted", at = @At("RETURN"))
    public void asSorted(CallbackInfoReturnable<RecipeGroupHolderMixin> cir) {
        RecipeGroupHolderMixin registry = cir.getReturnValue();
        registry.backupRecipes.putAll(backupRecipes);
    }

}
