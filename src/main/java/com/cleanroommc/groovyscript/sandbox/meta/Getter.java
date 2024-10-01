package com.cleanroommc.groovyscript.sandbox.meta;

import com.cleanroommc.groovyscript.api.Hidden;
import groovy.lang.MetaMethod;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ReflectionCache;

import java.lang.reflect.Modifier;
import java.util.function.Function;

public class Getter<T, S> extends MetaMethod implements Hidden {

    public static final Class<?>[] PARAMS = {};
    public static final CachedClass[] PARAM_CACHED = {};

    private final String name;
    private final Class<T> returnType;
    private final Class<S> owner;
    private final Function<S, T> getter;

    public Getter(String name, Class<T> returnType, Class<S> owner, Function<S, T> getter) {
        super(PARAMS);
        this.name = name;
        this.returnType = returnType;
        this.owner = owner;
        this.getter = getter;
        setParametersTypes(PARAM_CACHED);
    }

    @Override
    public int getModifiers() {
        return Modifier.PUBLIC;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Class<T> getReturnType() {
        return this.returnType;
    }

    @Override
    public CachedClass getDeclaringClass() {
        return ReflectionCache.getCachedClass(this.owner);
    }

    @Override
    public Object invoke(Object object, Object[] arguments) {
        S self = object == null ? null : (S) object;
        return this.getter.apply(self);
    }

    @Override
    public boolean isHidden() {
        return true;
    }
}
