package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import groovy.lang.MetaClassImpl;
import groovy.lang.MetaClassRegistry;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class ClassScriptMetaClass extends MetaClassImpl {

    public ClassScriptMetaClass(MetaClassRegistry registry, Class theClass) {
        super(registry, theClass);
    }

    @Override
    public Object invokeMissingProperty(Object instance, String propertyName, Object optionalValue, boolean isGetter) {
        Object o = GroovyScript.getSandbox().getBindings().get(propertyName);
        if (o != null) {
            return isGetter ? o : null;
        }
        return super.invokeMissingProperty(instance, propertyName, optionalValue, isGetter);
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
