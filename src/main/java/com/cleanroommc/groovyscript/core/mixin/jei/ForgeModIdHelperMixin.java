package com.cleanroommc.groovyscript.core.mixin.jei;

import com.cleanroommc.groovyscript.GroovyScript;
import mezz.jei.startup.ForgeModIdHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ForgeModIdHelper.class, remap = false)
public class ForgeModIdHelperMixin {

    @Inject(method = "getModNameForModId", at = @At("HEAD"), cancellable = true)
    public void getModId(String modId, CallbackInfoReturnable<String> cir) {
        if (modId.equals(GroovyScript.getRunConfig().getPackId())) {
            cir.setReturnValue(GroovyScript.getRunConfig().getPackName());
        }
    }
}
