package com.cleanroommc.groovyscript.sandbox.interception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class InterceptionManager {

    public static final InterceptionManager INSTANCE = new InterceptionManager();

    private final List<String> bannedPackages = new ArrayList<>();
    private final Set<Class<?>> bannedClasses = new HashSet<>();
    private final Map<Class<?>, Set<String>> bannedMethods = new HashMap<>();

    private static final Logger LOG = LogManager.getLogger("GroovySecurity");

    private InterceptionManager() {
        initDefaults();
    }

    public void initDefaults() {
        banPackage("java.io");
        banPackage("java.nio");
        banPackage("java.lang.reflect");
        banPackage("java.lang.invoke");
        banPackage("java.net");
        banPackage("java.rmi");
        banPackage("java.security");
        banPackage("groovy");
        banClasses(Runtime.class, ClassLoader.class);
        banMethods(System.class, "exit");
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
        banMethods(clazz, Arrays.asList(method));
    }

    public void banMethods(Class<?> clazz, Collection<String> method) {
        bannedMethods.computeIfAbsent(clazz, key -> new HashSet<>()).addAll(method);
    }

    public boolean isValid(Class<?> clazz, String methodName) {
        return isValidPackage(clazz) &&
                isValidClass(clazz) &&
                isValidMethod(clazz, methodName);
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
        return !bannedClasses.contains(clazz);
    }

    public boolean isValidMethod(Class<?> receiver, String method) {
        Set<String> methods = bannedMethods.get(receiver);
        return methods == null || !methods.contains(method);
    }
}
