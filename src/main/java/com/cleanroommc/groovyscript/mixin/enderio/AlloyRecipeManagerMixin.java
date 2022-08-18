package com.cleanroommc.groovyscript.mixin.enderio;

import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.sandbox.SandboxRunner;
import crazypants.enderio.base.recipe.IManyToOneRecipe;
import crazypants.enderio.base.recipe.alloysmelter.AlloyRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AlloyRecipeManager.class, remap = false)
public class AlloyRecipeManagerMixin {

    @Inject(method = "addRecipe(Lcrazypants/enderio/base/recipe/IManyToOneRecipe;)V", at = @At("RETURN"))
    private void afterAddRecipe(IManyToOneRecipe recipe, CallbackInfo ci) {
        if (SandboxRunner.isCurrentlyRunning()) {
            ModSupport.ENDER_IO.get().alloySmelter.addBackup(recipe);
        }
    }

}
