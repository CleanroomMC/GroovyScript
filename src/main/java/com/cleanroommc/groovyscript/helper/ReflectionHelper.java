package com.cleanroommc.groovyscript.helper;

import com.cleanroommc.groovyscript.api.GroovyLog;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectionHelper {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static Field modifiersField;

    public static boolean setFinal(Field field, boolean isFinal) throws Throwable {
        int m = field.getModifiers();
        if (Modifier.isFinal(m) == isFinal) return false;
        if (modifiersField == null) {
            modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
        }
        if (isFinal) m |= Modifier.FINAL;
        else m &= ~Modifier.FINAL;
        LOOKUP.unreflectSetter(modifiersField).invokeExact(field, m);
        return true;
    }

    public static MethodHandle resolveSetter(Field field) throws Throwable {
        return LOOKUP.unreflectSetter(field);
    }

    public static void setField(Field field, Object owner, Object value) throws Throwable {
        if (!field.isAccessible()) field.setAccessible(true);
        setFinal(field, false);
        MethodHandle mh = resolveSetter(field);
        mh.invoke(owner, value);
    }

    public static void setStaticField(Field field, Object value) throws Throwable {
        if (!field.isAccessible()) field.setAccessible(true);
        setFinal(field, false);
        MethodHandle mh = resolveSetter(field);
        mh.invoke(value);
    }

    public static Object getField(Field field, Object owner) throws Throwable {
        if (!field.isAccessible()) field.setAccessible(true);
        MethodHandle mh = LOOKUP.unreflectGetter(field);
        return mh.invoke(owner);
    }

    public static Object getStaticField(Field field) throws Throwable {
        if (!field.isAccessible()) field.setAccessible(true);
        MethodHandle mh = LOOKUP.unreflectGetter(field);
        return mh.invoke();
    }

    public static void setField(Object owner, String name, Object value) {
        try {
            Class<?> clazz = owner instanceof Class ? (Class<?>) owner : owner.getClass();
            Field field = clazz.getField(name);
            if (Modifier.isStatic(field.getModifiers())) setStaticField(field, value);
            setField(field, owner, value);
        } catch (Throwable e) {
            GroovyLog.get().errorMC("Error setting field {} in {}!", name, owner);
        }
    }

    public static Object getField(Object owner, String name) {
        try {
            Class<?> clazz = owner instanceof Class ? (Class<?>) owner : owner.getClass();
            Field field = clazz.getField(name);
            if (Modifier.isStatic(field.getModifiers())) owner = null;
            return getField(field, owner);
        } catch (Throwable e) {
            GroovyLog.get().errorMC("Error setting field {} in {}!", name, owner);
            return null;
        }
    }
}
