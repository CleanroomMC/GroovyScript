package com.cleanroommc.groovyscript.mixin.enderio;

import com.cleanroommc.groovyscript.compat.ModSupport;
import com.cleanroommc.groovyscript.compat.enderio.AlloySmelter;
import com.cleanroommc.groovyscript.compat.enderio.EnderIO;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import crazypants.enderio.base.recipe.IManyToOneRecipe;
import crazypants.enderio.base.recipe.alloysmelter.AlloyRecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AlloyRecipeManager.class)
public class AlloyRecipeManagerMixin {

    @Inject(method = "addRecipe(Lcrazypants/enderio/base/recipe/IManyToOneRecipe;)V", at = @At("RETURN"))
    private void afterAddRecipe(IManyToOneRecipe recipe, CallbackInfo ci) {
        if (ModSupport.ENDER_IO.getProperty(EnderIO.class).AlloySmelter.isCapturingRecipe()) {
            ReloadableRegistryManager.markScriptRecipe(AlloySmelter.class, recipe);
        }
    }

}
