package com.cleanroommc.groovyscript.sandbox.expand;

import groovy.lang.Closure;
import groovy.lang.MetaMethod;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ReflectionCache;
import org.codehaus.groovy.runtime.InvokerHelper;

import java.lang.reflect.Modifier;

public class ClosureMetaMethod extends MetaMethod {

    private final Closure<?> closure;
    private final String name;
    private final Class<?> declaringClass;

    public ClosureMetaMethod(Closure<?> closure, String name, Class<?> declaringClass) {
        super(closure.getParameterTypes());
        this.closure = closure;
        this.name = name;
        this.declaringClass = declaringClass;
    }

    @Override
    public int getModifiers() {
        return Modifier.PUBLIC;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class getReturnType() {
        return Object.class;
    }

    @Override
    public CachedClass getDeclaringClass() {
        return ReflectionCache.getCachedClass(declaringClass);
    }

    @Override
    public Object invoke(Object object, Object[] arguments) {
        Closure cloned = (Closure) closure.clone();
        cloned.setDelegate(object);
        arguments = coerceArgumentsToClasses(arguments);
        return InvokerHelper.invokeMethod(cloned, "call", arguments);
    }
}
