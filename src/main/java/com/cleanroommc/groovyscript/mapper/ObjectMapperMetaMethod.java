package com.cleanroommc.groovyscript.mapper;

import com.cleanroommc.groovyscript.sandbox.expand.IDocumented;
import groovy.lang.MetaMethod;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ReflectionCache;

import java.lang.reflect.Modifier;

public class ObjectMapperMetaMethod extends MetaMethod implements IDocumented {

    private final AbstractObjectMapper<?> closure;
    private final Class<?> owner;

    ObjectMapperMetaMethod(AbstractObjectMapper<?> closure, Class<?>[] nativeParamTypes, Class<?> owner) {
        super(nativeParamTypes);
        this.closure = closure;
        this.nativeParamTypes = nativeParamTypes;
        this.owner = owner;
    }

    @Override
    public int getModifiers() {
        return Modifier.PUBLIC;
    }

    @Override
    public String getName() {
        return this.closure.getName();
    }

    @Override
    public Class<?> getReturnType() {
        return this.closure.getReturnType();
    }

    @Override
    public CachedClass getDeclaringClass() {
        return ReflectionCache.getCachedClass(this.owner);
    }

    @Override
    public Object invoke(Object object, Object[] arguments) {
        arguments = coerceArgumentsToClasses(arguments);
        return this.closure.call(arguments);
    }

    @Override
    public String getDocumentation() {
        return this.closure.getDocumentation();
    }
}
