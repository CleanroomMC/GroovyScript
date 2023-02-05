package com.cleanroommc.groovyscript.sandbox.interception;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import groovy.lang.Binding;
import groovy.lang.Script;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.codehaus.groovy.runtime.NullObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class InterceptionManager {

    public static final InterceptionManager INSTANCE = new InterceptionManager();

    private final List<String> bannedPackages = new ArrayList<>();
    private final Set<Class<?>> bannedClasses = new ObjectOpenHashSet<>();
    private final Map<Class<?>, Set<String>> bannedMethods = new Object2ObjectOpenHashMap<>();
    private final Set<Class<?>> whiteListedClasses = new ObjectOpenHashSet<>();

    private InterceptionManager() {
        initDefaults();
    }

    public void initDefaults() {
        this.whiteListedClasses.add(NullObject.class);
        this.whiteListedClasses.add(Binding.class);

        banPackage("java.lang.reflect");
        banPackage("java.lang.invoke");
        banPackage("java.net");
        banPackage("java.rmi");
        banPackage("java.security");
        banPackage("groovy");
        banPackage("org.codehaus.groovy");
        banPackage("org.kohsuke");
        banPackage("sun."); // sun contains so many classes where some of them seem useful and others can break EVERYTHING, so im just gonna ban all because im lazy
        banPackage("javax.net");
        banPackage("javax.security");
        banPackage("javax.script");
        banPackage("org.spongepowered");
        banPackage("zone.rong.mixinbooter");
        banClasses(Runtime.class, ClassLoader.class);
        banMethods(System.class, "exit", "gc");

        // mod specific
        banPackage("com.cleanroommc.groovyscript.command");
        banPackage("com.cleanroommc.groovyscript.core");
        banPackage("com.cleanroommc.groovyscript.registry");
        banPackage("com.cleanroommc.groovyscript.sandbox");
    }

    public void banPackage(String packageName) {
        bannedPackages.add(packageName);
    }

    public void banClass(Class<?> clazz) {
        bannedClasses.add(clazz);
    }

    public void banClasses(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            banClass(clazz);
        }
    }

    public void banMethods(Class<?> clazz, String... method) {
        Collections.addAll(bannedMethods.computeIfAbsent(clazz, key -> new ObjectOpenHashSet<>()), method);
    }

    public void banMethods(Class<?> clazz, Collection<String> method) {
        bannedMethods.computeIfAbsent(clazz, key -> new ObjectOpenHashSet<>()).addAll(method);
    }

    public boolean isValid(Method method) {
        return isValidMethod(method.getDeclaringClass(), method.getName()) &&
                !method.isAnnotationPresent(GroovyBlacklist.class);
    }

    public boolean isValid(Field field) {
        return !field.isAnnotationPresent(GroovyBlacklist.class);
    }

    public boolean isValid(Class<?> clazz) {
        return Script.class.isAssignableFrom(clazz) ||
                this.whiteListedClasses.contains(clazz) ||
                (isValidClass(clazz) && isValidPackage(clazz));
    }

    public boolean isValidPackage(Class<?> clazz) {
        String className = clazz.getName();
        for (String bannedPackage : bannedPackages) {
            if (className.startsWith(bannedPackage)) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidClass(Class<?> clazz) {
        return !bannedClasses.contains(clazz) && !clazz.isAnnotationPresent(GroovyBlacklist.class);
    }

    public boolean isValidMethod(Class<?> receiver, String method) {
        Set<String> methods = bannedMethods.get(receiver);
        return methods == null || !methods.contains(method);
    }
}
