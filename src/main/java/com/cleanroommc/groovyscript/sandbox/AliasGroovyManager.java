package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.api.wrapper.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AliasGroovyManager {

    private static final Map<String, Class<?>> aliasClasses = new HashMap<>();
    private static final Map<Class<?>, Map<String, String>> aliasMethods = new HashMap<>();

    public static void registerAliasClass(Class<?> clazz, String aliasName) {
        if (clazz.getName().equals(aliasName)) {
            throw new IllegalArgumentException();
        }
        aliasClasses.put(aliasName, clazz);
    }

    public static void registerAliasMethod(Class<?> clazz, String originalName, String aliasName) {
        aliasMethods.computeIfAbsent(clazz, key -> new HashMap<>()).put(aliasName, originalName);
    }

    @Nullable
    public static Class<?> getClass(String clazz) {
        return aliasClasses.get(clazz);
    }

    @Nullable
    public static Class<?> getClass(Class<?> clazz) {
        return getClass(clazz.getName());
    }

    public static Class<?> getClassOrDefault(Class<?> clazz) {
        Class<?> clazz1 = getClass(clazz);
        return clazz1 == null ? clazz : clazz1;
    }

    public static Iterator<Map.Entry<String, Class<?>>> getClassAliasIterator() {
        return aliasClasses.entrySet().iterator();
    }
}
