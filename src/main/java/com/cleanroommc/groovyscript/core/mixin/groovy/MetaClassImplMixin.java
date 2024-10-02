package com.cleanroommc.groovyscript.core.mixin.groovy;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.IDynamicGroovyProperty;
import com.cleanroommc.groovyscript.sandbox.meta.ClassMetaClass;
import com.cleanroommc.groovyscript.sandbox.security.GroovySecurityManager;
import groovy.lang.*;
import org.codehaus.groovy.runtime.metaclass.MetaClassRegistryImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.Map;

@Mixin(value = MetaClassImpl.class, remap = false)
public abstract class MetaClassImplMixin {

    @Shadow
    protected abstract Object doInvokeMethod(Class<?> sender, Object object, String methodName, Object[] originalArguments,
                                             boolean isCallToSuper, boolean fromInsideClass);

    @Shadow
    protected abstract Object invokeMissingMethod(Object instance, String methodName, Object[] arguments, RuntimeException original,
                                                  boolean isCallToSuper);

    @Shadow
    protected abstract MetaProperty getMetaProperty(String name, boolean useStatic);

    @Mutable
    @Shadow
    @Final
    private MetaMethod[] myNewMetaMethods;

    @Mutable
    @Shadow
    @Final
    private MetaMethod[] additionalMetaMethods;

    @Shadow protected MetaClassRegistry registry;

    @Inject(method = "<init>(Ljava/lang/Class;[Lgroovy/lang/MetaMethod;)V", at = @At("TAIL"))
    public void removeBlacklistedAdditional(Class<?> theClass, MetaMethod[] add, CallbackInfo ci) {
        if (additionalMetaMethods.length > 0) {
            MetaMethod[] mms = new MetaMethod[additionalMetaMethods.length];
            int i = 0;
            for (MetaMethod mm : additionalMetaMethods) {
                if (GroovySecurityManager.INSTANCE.isValid(mm)) {
                    mms[i++] = mm;
                }
            }
            if (i != additionalMetaMethods.length) {
                additionalMetaMethods = Arrays.copyOf(mms, i);
            }
        }
        if (myNewMetaMethods.length > 0) {
            MetaMethod[] mms = new MetaMethod[myNewMetaMethods.length];
            int i = 0;
            for (MetaMethod mm : myNewMetaMethods) {
                if (GroovySecurityManager.INSTANCE.isValid(mm)) {
                    mms[i++] = mm;
                }
            }
            if (i != myNewMetaMethods.length) {
                myNewMetaMethods = Arrays.copyOf(mms, i);
            }
        }
    }

    @Inject(method = "invokeMethod(Ljava/lang/Class;Ljava/lang/Object;Ljava/lang/String;[Ljava/lang/Object;ZZ)Ljava/lang/Object;",
            at = @At("HEAD"),
            cancellable = true)
    public void invokeMethod(Class<?> sender, Object object, String methodName, Object[] arguments, boolean isCallToSuper,
                             boolean fromInsideClass, CallbackInfoReturnable<Object> cir) {
        try {
            cir.setReturnValue(doInvokeMethod(sender, object, methodName, arguments, isCallToSuper, fromInsideClass));
        } catch (MissingMethodException mme) {
            throw new GroovyRuntimeException(mme);
        }
    }

    @Inject(method = "invokeMissingProperty", at = @At("HEAD"), cancellable = true)
    public void invokeMissingProperty(Object instance, String propertyName, Object optionalValue, boolean isGetter,
                                      CallbackInfoReturnable<Object> cir) {
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
        if ((Object) this instanceof ClassMetaClass cmc) {
            cmc.invokeStaticMissingMethod(sender, methodName, arguments, cir);
        }
    }

    /**
     * @author brachy
     * @reason class scripts being unable to use bindings and this method calling closures improperly
     */
    @Overwrite
    private Object invokePropertyOrMissing(Object object, String methodName, Object[] originalArguments, boolean fromInsideClass,
                                           boolean isCallToSuper) {
        MetaProperty metaProperty = getMetaProperty(methodName, false);

        Object value = null;
        if (metaProperty != null) {
            value = metaProperty.getProperty(object);
        } else if (object instanceof Map) {
            value = ((Map<?, ?>) object).get(methodName);
        } else if (object instanceof Script) {
            value = ((Script) object).getBinding().getVariables().get(methodName);
        } else if (!isCallToSuper && object instanceof IDynamicGroovyProperty dynamicGroovyProperty) {
            // TODO remove in 1.2.0
            value = dynamicGroovyProperty.getProperty(methodName);
        } else if (object instanceof GroovyObject) {
            value = GroovyScript.getSandbox().getBindings().get(methodName);
        }

        if (value instanceof Closure<?> closure) {
            return closure.call(originalArguments);
        }

        if (value != null && !(value instanceof Map) && !methodName.equals("call")) {
            try {
                MetaClass metaClass = ((MetaClassRegistryImpl) registry).getMetaClass(value);
                return metaClass.invokeMethod(value, "call", originalArguments); // delegate to call method of property value
            } catch (MissingMethodException mme) {
                // ignore
            }
        }

        return invokeMissingMethod(object, methodName, originalArguments, null, isCallToSuper);
    }
}
