package com.cleanroommc.groovyscript.core.mixin.groovy;

import com.cleanroommc.groovyscript.api.IDynamicGroovyProperty;
import groovy.lang.MetaClassImpl;
import groovy.lang.Script;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MetaClassImpl.class, remap = false)
public class MetaClassImplMixin {

    @Inject(method = "invokeMissingProperty", at = @At("HEAD"), cancellable = true)
    public void invokeMissingProperty(Object instance, String propertyName, Object optionalValue, boolean isGetter, CallbackInfoReturnable<Object> cir) {
        if (instance instanceof IDynamicGroovyProperty) {
            if (isGetter) {
                Object prop = ((IDynamicGroovyProperty) instance).getProperty(propertyName);
                if (prop != null) {
                    cir.setReturnValue(prop);
                }
            } else if (((IDynamicGroovyProperty) instance).setProperty(propertyName, optionalValue)) {
                cir.setReturnValue(null);
            }
        }
    }

    @Inject(method = "invokeStaticMissingMethod", at = @At("HEAD"), cancellable = true)
    public void invokeStaticMissingMethod(Class<?> sender, String methodName, Object[] arguments, CallbackInfoReturnable<Object> cir) {
        if (sender.getSuperclass() != Script.class && "main".equals(methodName)) {
            cir.setReturnValue(null);
        }
    }
}
