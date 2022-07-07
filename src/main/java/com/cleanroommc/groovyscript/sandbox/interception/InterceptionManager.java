package com.cleanroommc.groovyscript.sandbox.interception;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.sandbox.SimpleGroovyInterceptor;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassImpl;
import groovy.lang.MetaMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.groovy.reflection.CachedMethod;
import org.codehaus.groovy.runtime.metaclass.ReflectionMetaMethod;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
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
        banPackage("org.codehaus.groovy");
        banPackage("sun"); // sun contains so many classes where some of them seem useful and others can break EVERYTHING, so im just gonna ban all because im lazy
        banPackage("javax.net");
        banPackage("javax.security");
        banPackage("javax.script");
        banPackage("org.spongepowered");
        banPackage("zone.rong.mixinbooter");
        banClasses(Runtime.class, ClassLoader.class);
        banMethods(System.class, "exit");
        banMethods(System.class, "gc");
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
        return !bannedClasses.contains(clazz) && !isBlacklistedClass(clazz);
    }

    public boolean isValidMethod(Class<?> receiver, String method) {
        Set<String> methods = bannedMethods.get(receiver);
        return methods == null || !methods.contains(method);
    }

    public boolean isBlacklistedClass(Class<?> receiver) {
        return receiver.isAnnotationPresent(GroovyBlacklist.class);
    }

    public boolean isBlacklistedMethod(Class<?> receiver, String method, Object... args) {
        AnnotatedElement method1 = findMethod(receiver, method, args);
        if (method1 == null) {
            GroovyScript.LOGGER.error("Could not find method {} in {}", method, receiver.getName());
        }
        return method1 != null && method1.isAnnotationPresent(GroovyBlacklist.class);
    }

    public boolean isBlacklistedField(Class<?> receiver, String field) throws NoSuchFieldException {
        Field field1 = receiver.getField(field);
        return field1.isAnnotationPresent(GroovyBlacklist.class);
    }

    private static Class<?>[] argsToClassArray(Object... args) {
        Class<?>[] types = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i].getClass();
        }
        return types;
    }

    private AnnotatedElement findMethod(Class<?> receiver, String methodName, Object[] args) {
        MetaClass metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(receiver);
        MetaMethod method;
        if (methodName.equals(SimpleGroovyInterceptor.CONSTRUCTOR_METHOD)) {
            if (metaClass instanceof MetaClassImpl) {
                method = ((MetaClassImpl) metaClass).retrieveConstructor(args);
                if (method instanceof MetaClassImpl.MetaConstructor) {
                    return ((MetaClassImpl.MetaConstructor) method).getCachedConstrcutor().getCachedConstructor();
                }
            }
            return null;
        }
        method = metaClass.getMetaMethod(methodName, args);
        if (method == null) {
            method = metaClass.getStaticMetaMethod(methodName, args);
        }
        if (method == null) {
            return null;
        }

        while (method instanceof ReflectionMetaMethod) {
            method = ((ReflectionMetaMethod) method).getCachedMethod();
        }
        if (method instanceof CachedMethod) {
            return ((CachedMethod) method).getCachedMethod();
        }
        return null;
    }
}
