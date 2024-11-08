package com.cleanroommc.groovyscript.sandbox.meta;

import com.cleanroommc.groovyscript.api.Hidden;
import groovy.lang.MetaMethod;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ReflectionCache;

import java.lang.reflect.Modifier;
import java.util.function.BiConsumer;

public class Setter<T, S> extends MetaMethod implements Hidden {

    private final String name;
    private final Class<S> owner;
    private final BiConsumer<S, T> setter;

    public Setter(String name, Class<T> paramType, Class<S> owner, BiConsumer<S, T> setter) {
        super(new Class[]{
                paramType
        });
        this.name = name;
        this.owner = owner;
        this.setter = setter;
        setParametersTypes(new CachedClass[]{
                ReflectionCache.getCachedClass(paramType)
        });
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
    public Class<Void> getReturnType() {
        return void.class;
    }

    @Override
    public CachedClass getDeclaringClass() {
        return ReflectionCache.getCachedClass(this.owner);
    }

    @Override
    public Object invoke(Object object, Object[] arguments) {
        S self = object == null ? null : (S) object;
        T arg = (T) arguments[0];
        this.setter.accept(self, arg);
        return null;
    }

    @Override
    public boolean isHidden() {
        return true;
    }
}
