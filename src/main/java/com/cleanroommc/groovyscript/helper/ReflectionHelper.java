package com.cleanroommc.groovyscript.helper;

import com.cleanroommc.groovyscript.api.GroovyLog;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ReflectionHelper {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static Field fieldModifiersField;
    private static Field methodModifiersField;
    private static MethodHandle fieldModifiersSetter;
    private static MethodHandle methodModifiersSetter;

    public static Field getFieldModifiersField() {
        if (fieldModifiersField == null) {
            try {
                fieldModifiersField = Field.class.getDeclaredField("modifiers");
            } catch (NoSuchFieldException e) {
                // something is very wrong if this crashes
                throw new RuntimeException(e);
            }
            fieldModifiersField.setAccessible(true);
        }
        return fieldModifiersField;
    }

    public static Field getMethodModifiersField() {
        if (methodModifiersField == null) {
            try {
                methodModifiersField = Method.class.getDeclaredField("modifiers");
            } catch (NoSuchFieldException e) {
                // something is very wrong if this crashes
                throw new RuntimeException(e);
            }
            methodModifiersField.setAccessible(true);
        }
        return methodModifiersField;
    }

    private static MethodHandle getFieldModifiersSetter() {
        if (fieldModifiersSetter == null) {
            try {
                fieldModifiersSetter = LOOKUP.unreflectSetter(getFieldModifiersField());
            } catch (IllegalAccessException e) {
                // something is very wrong if this crashes
                throw new RuntimeException(e);
            }
        }
        return fieldModifiersSetter;
    }

    public static MethodHandle getMethodModifiersSetter() {
        if (methodModifiersSetter == null) {
            try {
                methodModifiersSetter = LOOKUP.unreflectSetter(getMethodModifiersField());
            } catch (IllegalAccessException e) {
                // something is very wrong if this crashes
                throw new RuntimeException(e);
            }
        }
        return methodModifiersSetter;
    }

    public static void setModifiers(Field field, int modifiers) {
        try {
            getFieldModifiersSetter().invokeExact(field, modifiers);
        } catch (Throwable e) {
            // unlikely to crash
            throw new RuntimeException(e);
        }
    }

    public static void setModifiers(Method method, int modifiers) {
        try {
            getMethodModifiersSetter().invokeExact(method, modifiers);
        } catch (Throwable e) {
            // unlikely to crash
            throw new RuntimeException(e);
        }
    }

    public static boolean setFinal(Field field, boolean isFinal) {
        int m = field.getModifiers();
        if (Modifier.isFinal(m) == isFinal) return false;
        if (isFinal) m |= Modifier.FINAL;
        else m &= ~Modifier.FINAL;
        setModifiers(field, m);
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
            GroovyLog.get().errorMC("Error getting field {} in {}!", name, owner);
            return null;
        }
    }

    public static int makeModifiersPublic(int modifiers) {
        if (!Modifier.isPublic(modifiers)) modifiers |= Modifier.PUBLIC;
        if (Modifier.isProtected(modifiers)) modifiers &= ~Modifier.PROTECTED;
        if (Modifier.isPrivate(modifiers)) modifiers &= ~Modifier.PRIVATE;
        return modifiers;
    }

    public static void makeFieldPublic(Field field) {
        int mod = field.getModifiers();
        int newMod = makeModifiersPublic(mod);
        if (mod != newMod) setModifiers(field, newMod);
    }

    public static void makeMethodPublic(Method method) {
        int mod = method.getModifiers();
        int newMod = makeModifiersPublic(mod);
        if (mod != newMod) setModifiers(method, newMod);
    }
}
