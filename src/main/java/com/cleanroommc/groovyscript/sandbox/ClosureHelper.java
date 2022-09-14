package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import groovy.lang.Closure;
import org.jetbrains.annotations.Nullable;

public class ClosureHelper {

    @Nullable
    public static <T> T call(Closure<T> closure, Object... args) {
        return GroovyScript.getSandbox().runClosure(closure, args);
    }

    @Nullable
    public static <T> T call(Class<T> expectedType, Closure<?> closure, Object... args) {
        Object o = call(closure, args);
        if (o != null && expectedType.isAssignableFrom(o.getClass())) {
            return (T) o;
        }
        return null;
    }

    public static <T> T call(T defaultValue, Closure<?> closure, Object... args) {
        Object o = call(closure, args);
        if (o != null && o.getClass().isInstance(defaultValue)) {
            return (T) o;
        }
        return defaultValue;
    }
}
