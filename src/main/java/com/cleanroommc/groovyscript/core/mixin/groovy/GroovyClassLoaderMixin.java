package com.cleanroommc.groovyscript.core.mixin.groovy;

import com.cleanroommc.groovyscript.GroovyScript;
import groovy.lang.GroovyClassLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.URL;

/**
 * If a script depends on another script and the there is a compiled cache for the script, it needs to be loaded manually.
 */
@Mixin(value = GroovyClassLoader.class, remap = false)
public class GroovyClassLoaderMixin {

    @Inject(method = "recompile", at = @At("HEAD"), cancellable = true)
    public void onRecompile(URL source, String className, Class<?> oldClass, CallbackInfoReturnable<Class<?>> cir) {
        if (source != null && oldClass == null) {
            Class<?> c = GroovyScript.getSandbox().onRecompileClass((GroovyClassLoader) (Object) this, source, className);
            if (c != null) {
                cir.setReturnValue(c);
            }
        }
    }
}
