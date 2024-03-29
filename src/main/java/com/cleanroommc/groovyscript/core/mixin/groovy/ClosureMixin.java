package com.cleanroommc.groovyscript.core.mixin.groovy;

import com.cleanroommc.groovyscript.GroovyScript;
import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Closure.class, remap = false)
public abstract class ClosureMixin<V> extends GroovyObjectSupport {

    @Inject(method = "call([Ljava/lang/Object;)Ljava/lang/Object;", at = @At("HEAD"), cancellable = true)
    public void call(Object[] arguments, CallbackInfoReturnable<V> cir) {
        // redirect closure call to properly catch and log errors
        cir.setReturnValue(GroovyScript.getSandbox().runClosure((Closure<? extends V>) (Object) this, arguments));
    }
}
