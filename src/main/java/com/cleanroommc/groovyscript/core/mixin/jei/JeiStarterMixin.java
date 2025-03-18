package com.cleanroommc.groovyscript.core.mixin.jei;

import com.cleanroommc.groovyscript.compat.mods.jei.JeiPlugin;
import mezz.jei.startup.JeiStarter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = JeiStarter.class, remap = false)
public abstract class JeiStarterMixin {

    /**
     * @reason run GroovyScript removal methods after all plugins have been registered to allow users to customize information related to all plugins
     * @see JeiPlugin#afterRegister()
     */
    @Inject(method = "registerPlugins", at = @At("TAIL"))
    private static void grs$onRegisterPlugins(CallbackInfo ci) {
        JeiPlugin.afterRegister();
    }

    /**
     * @reason run GroovyScript removal methods after all plugins have acted on runtime to allow users to customize information related to all plugins
     * @see JeiPlugin#afterRuntimeAvailable()
     */
    @Inject(method = "sendRuntime", at = @At("TAIL"))
    private static void grs$onSendRuntime(CallbackInfo ci) {
        JeiPlugin.afterRuntimeAvailable();
    }
}
