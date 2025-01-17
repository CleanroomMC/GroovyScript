package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.sandbox.expand.LambdaClosure;
import groovy.lang.Closure;
import org.codehaus.groovy.runtime.ConvertedClosure;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.function.Function;

public class ClosureHelper {

    private static final Object DUMMY = new Object();
    private static Field h;

    public static @Nullable <T> T call(Closure<T> closure, Object... args) {
        return closure.call(args);
    }

    public static @Nullable <T> T call(Class<T> expectedType, Closure<?> closure, Object... args) {
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

    /**
     * The code inside a closure will try to find variables in its owner by default.
     * This allows to add another variable holder and sets it as its primary variable holder.
     * After this method is called, variables will be first searched in the given environment and then in the owner object.
     * The owner object is usually a {@link groovy.lang.Script} instance.
     *
     * @param closure     closure
     * @param environment variable holder
     * @param force       force overwrite current variable holder
     * @return the given closure
     */
    public static <T> Closure<T> withEnvironment(Closure<T> closure, Object environment, boolean force) {
        if (force || closure.getDelegate() == null) {
            closure.setDelegate(environment);
            closure.setResolveStrategy(Closure.DELEGATE_FIRST);
        }
        return closure;
    }

    @GroovyBlacklist
    public static <T> Closure<T> of(Function<Object[], T> function) {
        return of(DUMMY, function);
    }

    @GroovyBlacklist
    public static <T> Closure<T> of(Object owner, Function<Object[], T> function) {
        return new LambdaClosure<>(owner, function);
    }

    /**
     * Extracts the underlying closure from a functional interface instance.
     *
     * @param functionalInterface a functional interface like {@link Function}
     * @return the underlying closure or null if there is no closure
     */
    @GroovyBlacklist
    public static @Nullable Closure<?> getUnderlyingClosure(Object functionalInterface) {
        if (!(functionalInterface instanceof Proxy)) return null; // not a closure
        if (h == null) {
            try {
                Method getFields = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
                getFields.setAccessible(true);
                for (Field field : (Field[]) getFields.invoke(Proxy.class, false)) {
                    if (field.getName().equals("h")) {
                        h = field;
                        h.setAccessible(true);
                        break;
                    }
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (h == null) {
                throw new IllegalStateException("Field h not found");
            }
        }
        try {
            InvocationHandler handler = (InvocationHandler) h.get(functionalInterface);
            if (handler instanceof ConvertedClosure convertedClosure) {
                return (Closure<?>) convertedClosure.getDelegate();
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            GroovyLog.get().exception("A reflection error occurred while trying to obtain a closure from a lambda.", e);
        }
        return null;
    }
}
