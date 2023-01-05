package com.cleanroommc.groovyscript.sandbox.interception;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.sandbox.FieldAccess;
import groovy.lang.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.groovy.reflection.CachedField;
import org.codehaus.groovy.reflection.CachedMethod;
import org.codehaus.groovy.runtime.metaclass.ReflectionMetaMethod;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

public class InterceptionManager {

    public static final InterceptionManager INSTANCE = new InterceptionManager();

    public static final String CONSTRUCTOR_METHOD = "<init>";

    private final List<String> bannedPackages = new ArrayList<>();
    private final Set<Class<?>> bannedClasses = new ObjectOpenHashSet<>();
    private final Map<Class<?>, Set<String>> bannedMethods = new Object2ObjectOpenHashMap<>();

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
        banPackage("org.kohsuke");
        banPackage("sun."); // sun contains so many classes where some of them seem useful and others can break EVERYTHING, so im just gonna ban all because im lazy
        banPackage("javax.net");
        banPackage("javax.security");
        banPackage("javax.script");
        banPackage("org.spongepowered");
        banPackage("zone.rong.mixinbooter");
        banClasses(Runtime.class, ClassLoader.class);
        banMethods(System.class, "exit", "gc");
        // TODO wrap and/or ban Minecraft and MinecraftServer

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

    public boolean isValid(Class<?> clazz, String methodName, Object... args) {
        return isValid(clazz, methodName, methodName, args);
    }

    public boolean isValid(Class<?> clazz, String methodName, String obfMethodName, Object... args) {
        return isValidClass(clazz) &&
                isValidPackage(clazz) &&
                isValidMethod(clazz, methodName) &&
                !isBlacklistedMethod(clazz, obfMethodName, args);
    }

    public boolean isValidConstructor(Class<?> clazz, Object... args) {
        return isValidClass(clazz) &&
                isValidPackage(clazz) &&
                isValidMethod(clazz, CONSTRUCTOR_METHOD) &&
                !isBlacklistedConstructor(clazz, args);
    }

    public boolean isValid(Class<?> clazz) {
        return isValidClass(clazz) && isValidPackage(clazz);
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
        if (receiver.getSuperclass() == Script.class) return false;
        if (receiver.isAnnotationPresent(GroovyBlacklist.class)) return true;
        AnnotatedElement method1 = findMethod(receiver, method, args);
        if (method1 == null) {
            GroovyScript.LOGGER.debug("Could not find method {} in {}", method, receiver.getName());
        }
        return method1 != null && method1.isAnnotationPresent(GroovyBlacklist.class);
    }

    public boolean isBlacklistedConstructor(Class<?> receiver, Object... args) {
        if (receiver.getSuperclass() == Script.class) return false;
        Constructor<?> constructor = findConstructor(receiver, args);
        if (constructor == null) {
            GroovyScript.LOGGER.debug("Could not find constructor in {}", receiver.getName());
        }
        return constructor != null && constructor.isAnnotationPresent(GroovyBlacklist.class);
    }

    public boolean isBlacklistedField(Class<?> receiver, String field, FieldAccess access) {
        if (receiver.getSuperclass() == Script.class) return false;
        if (receiver.isAnnotationPresent(GroovyBlacklist.class)) return true;
        AnnotatedElement field1 = findField(receiver, field, access);
        if (field1 == null) {
            GroovyScript.LOGGER.debug("Could not find field {} in {}", field, receiver.getName());
        }
        return field1 != null && field1.isAnnotationPresent(GroovyBlacklist.class);
    }

    private AnnotatedElement findMethod(Class<?> receiver, String methodName, Object[] args) {
        MetaClass metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(receiver);
        MetaMethod method = metaClass.getMetaMethod(methodName, args);
        if (method == null) {
            method = metaClass.getStaticMetaMethod(methodName, args);
            if (method == null) {
                return null;
            }
        }

        Method method1 = findMethod(method);
        if (method1 == null) {
            GroovyScript.LOGGER.debug("No method found for {}", methodName);
        }
        return method1;
    }

    private Method findMethod(MetaMethod method) {
        while (method instanceof ReflectionMetaMethod) {
            method = ((ReflectionMetaMethod) method).getCachedMethod();
        }
        if (method instanceof CachedMethod) {
            return ((CachedMethod) method).getCachedMethod();
        }
        return null;
    }

    private Constructor<?> findConstructor(Class<?> receiver, Object... args) {
        MetaClass metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(receiver);
        if (metaClass instanceof MetaClassImpl) {
            MetaMethod method = ((MetaClassImpl) metaClass).retrieveConstructor(args);
            if (method instanceof MetaClassImpl.MetaConstructor) {
                return ((MetaClassImpl.MetaConstructor) method).getCachedConstrcutor().getCachedConstructor();
            }
        }
        return null;
    }

    private AnnotatedElement findField(Class<?> receiver, String fieldName, FieldAccess access) {
        MetaClass metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(receiver);
        MetaProperty metaProperty = metaClass.getMetaProperty(fieldName);
        if (metaProperty == null) {
            return null;
        }
        if (metaProperty instanceof MetaBeanProperty) {
            CachedField field = ((MetaBeanProperty) metaProperty).getField();
            if (field != null) return field.getCachedField();
            MetaMethod method = access.isGetter() ?
                    ((MetaBeanProperty) metaProperty).getGetter() : access.isSetter() ?
                    ((MetaBeanProperty) metaProperty).getSetter() : null;
            return method == null ? null : findMethod(method);
        }
        if (metaProperty instanceof CachedField) {
            return ((CachedField) metaProperty).getCachedField();
        }
        GroovyScript.LOGGER.info("No field found for {}", metaProperty);
        return null;
    }

    private static Class<?>[] getArgTypes(Object... args) {
        Class<?>[] types = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i].getClass();
        }
        return types;
    }
}
