package com.cleanroommc.groovyscript.sandbox.meta;

import com.cleanroommc.groovyscript.GroovyScript;
import groovy.lang.MetaClassImpl;
import groovy.lang.MetaClassRegistry;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ClassMetaClass extends MetaClassImpl {

    public ClassMetaClass(MetaClassRegistry registry, Class theClass) {
        super(registry, theClass);
    }

    @Override
    protected Object invokeStaticMissingProperty(Object instance, String propertyName, Object optionalValue, boolean isGetter) {
        Object o = GroovyScript.getSandbox().getBindings().get(propertyName);
        if (o != null) {
            return isGetter ? o : null;
        }
        return super.invokeStaticMissingProperty(instance, propertyName, optionalValue, isGetter);
    }

    public void invokeStaticMissingMethod(Class<?> sender, String methodName, Object[] arguments, CallbackInfoReturnable<Object> cir) {
        if ("main".equals(methodName)) {
            cir.setReturnValue(null);
        }
    }

}
