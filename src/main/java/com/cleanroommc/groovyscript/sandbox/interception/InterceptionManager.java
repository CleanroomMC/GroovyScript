package com.cleanroommc.groovyscript.sandbox.interception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class InterceptionManager {

    public static final InterceptionManager INSTANCE = new InterceptionManager();

    private final List<String> bannedPackages = new ArrayList<>();
    private final Set<String> bannedClasses = new HashSet<>();
    private final Map<String, Set<String>> bannedMethods = new HashMap<>();

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
        bannedClasses.add(clazz.getName());
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
        bannedMethods.computeIfAbsent(clazz.getName(), key -> new HashSet<>()).addAll(method);
    }

    public boolean isValid(String className) {
        for (String bannedPackage : bannedPackages) {
            if (className.startsWith(bannedPackage)) {
                LOG.error("Calls to classes from the '{}' package are prohibited!", bannedPackage);
                return false;
            }
        }

        if (bannedClasses.contains(className)) {
            LOG.error("Calls to the class '{}' are prohibited!", className);
            return false;
        }
        return true;
    }

    public boolean isValid(String sourceName, int lineNumber, String className, String methodName, boolean isGroovyObject) throws SandboxSecurityException {
        for (String bannedPackage : bannedPackages) {
            if (className.startsWith(bannedPackage)) {
                LOG.error("Calls to classes from the '{}' package are prohibited!", bannedPackage);
                return false;
            }
        }

        if (bannedClasses.contains(className)) {
            LOG.error("Calls to the class '{}' are prohibited!", className);
            return false;
        }

        Set<String> methods = bannedMethods.get(className);

        if (methods == null || !methods.contains(methodName)) return true;
        LOG.error("Calls to the method '{}' in class '{}' are prohibited!", methodName, className);
        return false;
    }
}
