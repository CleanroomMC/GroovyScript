package com.cleanroommc.groovyscript.sandbox.security;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.sandbox.GroovyLogImpl;
import com.cleanroommc.groovyscript.sandbox.expand.LambdaClosure;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.ui.GroovyMain;
import groovy.ui.GroovySocketServer;
import groovy.util.Eval;
import groovy.util.GroovyScriptEngine;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.codehaus.groovy.runtime.FormatHelper;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.NullObject;
import org.codehaus.groovy.runtime.RegexSupport;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class GroovySecurityManager {

    public static final GroovySecurityManager INSTANCE = new GroovySecurityManager();

    private final List<String> bannedPackages = new ArrayList<>();
    private final Set<Class<?>> bannedClasses = new ObjectOpenHashSet<>();
    private final Map<Class<?>, Set<String>> bannedMethods = new Object2ObjectOpenHashMap<>();
    private final Set<Class<?>> whiteListedClasses = new ObjectOpenHashSet<>();

    private GroovySecurityManager() {
        initDefaults();
    }

    public void initDefaults() {
        unBanClasses(GroovyLogImpl.class, LambdaClosure.class);
        unBanClasses(NullObject.class, FormatHelper.class, GStringImpl.class, RegexSupport.class);

        banPackage("java.lang.reflect");
        banPackage("java.lang.invoke");
        banPackage("java.net");
        banPackage("java.rmi");
        banPackage("java.security");
        banPackage("groovy.grape");
        banPackage("groovy.beans");
        banPackage("groovy.cli");
        banPackage("groovyjarjar");
        banPackage("sun."); // sun contains so many classes where some of them seem useful and others can break EVERYTHING, so im just gonna ban all because im lazy
        banPackage("javax.net");
        banPackage("javax.security");
        banPackage("javax.script");
        banPackage("org.spongepowered");
        banPackage("zone.rong.mixinbooter");
        banClasses(Runtime.class, ClassLoader.class);
        banClasses(GroovyScriptEngine.class, Eval.class, GroovyMain.class, GroovySocketServer.class, GroovyShell.class, GroovyClassLoader.class);
        banMethods(System.class, "exit", "gc");

        // mod specific
        banPackage("com.cleanroommc.groovyscript.command");
        banPackage("com.cleanroommc.groovyscript.core");
        banPackage("com.cleanroommc.groovyscript.registry");
        banPackage("com.cleanroommc.groovyscript.sandbox");
    }

    public void unBanClass(Class<?> clazz) {
        whiteListedClasses.add(clazz);
    }

    public void unBanClasses(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            unBanClass(clazz);
        }
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
        return this.whiteListedClasses.contains(clazz) ||
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
