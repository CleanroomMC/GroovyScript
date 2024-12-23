package com.cleanroommc.groovyscript.sandbox.security;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.cleanroommc.groovyscript.sandbox.GroovyLogImpl;
import com.cleanroommc.groovyscript.sandbox.expand.LambdaClosure;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.MetaMethod;
import groovy.ui.GroovyMain;
import groovy.ui.GroovySocketServer;
import groovy.util.Eval;
import groovy.util.GroovyScriptEngine;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.apache.commons.io.FileUtils;
import org.codehaus.groovy.runtime.*;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class GroovySecurityManager {

    public static final GroovySecurityManager INSTANCE = new GroovySecurityManager();
    private static final String blacklistDesc = Type.getDescriptor(GroovyBlacklist.class);

    private final List<String> bannedPackages = new ArrayList<>();
    private final Set<String> bannedClasses = new ObjectOpenHashSet<>();
    private final Map<String, Set<String>> bannedMethods = new Object2ObjectOpenHashMap<>();
    private final Set<String> whiteListedClasses = new ObjectOpenHashSet<>();

    private GroovySecurityManager() {
        initDefaults();
    }

    public void initDefaults() {
        unBanClasses(GroovyLogImpl.class, LambdaClosure.class);
        unBanClasses(NullObject.class, FormatHelper.class, GStringImpl.class, RegexSupport.class);
        unBanClass(PrintWriter.class); // for print methods

        banPackage("java.lang.reflect");
        banPackage("java.lang.invoke");
        banPackage("java.net");
        banPackage("java.rmi");
        banPackage("java.security");
        banPackage("java.io");
        banPackage("java.nio.file");
        banPackage("java.nio.channels");
        banPackage("groovy.grape");
        banPackage("groovy.beans");
        banPackage("groovy.cli");
        banPackage("groovyjarjarantlr4.");
        banPackage("groovyjarjarasm.");
        banPackage("groovyjarjarpicocli.");
        banPackage("sun."); // sun contains so many classes where some of them seem useful and others can break EVERYTHING, so im just gonna ban all because im lazy
        banPackage("javax.net");
        banPackage("javax.security");
        banPackage("javax.script");
        banPackage("org.spongepowered");
        banPackage("zone.rong.mixinbooter");
        banPackage("net.minecraftforge.gradle");
        banClasses(Runtime.class, ClassLoader.class, Scanner.class);
        banClasses(GroovyScriptEngine.class, Eval.class, GroovyMain.class, GroovySocketServer.class, GroovyShell.class, GroovyClassLoader.class);
        banMethods(System.class, "exit", "gc", "setSecurityManager");
        banMethods(Class.class, "getResource", "getResourceAsStream");
        banMethods(String.class, "execute");
        banMethods(ProcessGroovyMethods.class, "execute");
        banClasses(FileUtils.class, org.apache.logging.log4j.core.util.FileUtils.class);

        // mod specific
        banPackage("com.cleanroommc.groovyscript.command");
        banPackage("com.cleanroommc.groovyscript.core");
        banPackage("com.cleanroommc.groovyscript.sandbox");
        banPackage("com.cleanroommc.groovyscript.server");
        banPackage("net.prominic");
        banMethods(IScriptReloadable.class, "onReload", "afterScriptLoad");
        banMethods(VirtualizedRegistry.class, "createRecipeStorage");
        banMethods(GroovyPropertyContainer.class, "initialize");
    }

    public void unBanClass(Class<?> clazz) {
        whiteListedClasses.add(clazz.getName());
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
        bannedClasses.add(clazz.getName());
    }

    public void banClasses(Class<?>... classes) {
        for (Class<?> clazz : classes) {
            banClass(clazz);
        }
    }

    public void banMethods(Class<?> clazz, String... method) {
        Collections.addAll(bannedMethods.computeIfAbsent(clazz.getName(), key -> new ObjectOpenHashSet<>()), method);
    }

    public void banMethods(Class<?> clazz, Collection<String> method) {
        bannedMethods.computeIfAbsent(clazz.getName(), key -> new ObjectOpenHashSet<>()).addAll(method);
    }

    public boolean isValid(Method method) {
        return isValidMethod(method.getDeclaringClass(), method.getName()) && !method.isAnnotationPresent(GroovyBlacklist.class);
    }

    public boolean isValid(MetaMethod method) {
        return isValidMethod(method.getDeclaringClass().getTheClass(), method.getName());
    }

    public boolean isValid(Field field) {
        return !field.isAnnotationPresent(GroovyBlacklist.class);
    }

    public boolean isValid(ClassNode classNode) {
        return this.whiteListedClasses.contains(classNode.name) || (!bannedClasses.contains(classNode.name) && !hasBlacklistAnnotation(classNode.visibleAnnotations) && isValidPackage(classNode.name));
    }

    public boolean isValid(Class<?> clazz) {
        return this.whiteListedClasses.contains(clazz.getName()) || (isValidClass(clazz) && isValidPackage(clazz));
    }

    public boolean isValidPackage(String className) {
        for (String bannedPackage : bannedPackages) {
            if (className.startsWith(bannedPackage)) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidPackage(Class<?> clazz) {
        return isValidPackage(clazz.getName());
    }

    public boolean isValidClass(Class<?> clazz) {
        return !bannedClasses.contains(clazz.getName()) && !clazz.isAnnotationPresent(GroovyBlacklist.class);
    }

    public boolean isValidMethod(Class<?> receiver, String method) {
        while (receiver != null && receiver != Object.class) {
            if (isMethodBannedFromClass(receiver.getName(), method)) return false;
            for (Class<?> interf : receiver.getInterfaces()) {
                if (isMethodBannedFromClass(interf.getName(), method)) return false;
            }
            receiver = receiver.getSuperclass();
        }
        return true;
    }

    public boolean isValidMethod(ClassNode receiver, String method) {
        if (isMethodBannedFromClass(receiver.name, method)) return false;
        if (receiver.interfaces != null) {
            for (String interf : receiver.interfaces) {
                if (isMethodBannedFromClass(interf, method)) return false;
            }
        }
        // unfortunately can't check all super classes here
        return receiver.name == null || !isMethodBannedFromClass(receiver.superName, method);
    }

    private boolean isMethodBannedFromClass(String receiver, String method) {
        Set<String> methods = bannedMethods.get(receiver);
        return methods != null && methods.contains(method);
    }

    public List<String> getBannedPackages() {
        return Collections.unmodifiableList(bannedPackages);
    }

    public Set<String> getBannedClasses() {
        return Collections.unmodifiableSet(bannedClasses);
    }

    public Map<String, Set<String>> getBannedMethods() {
        return Collections.unmodifiableMap(bannedMethods);
    }

    public Set<String> getWhiteListedClasses() {
        return Collections.unmodifiableSet(whiteListedClasses);
    }

    public static boolean hasBlacklistAnnotation(List<AnnotationNode> annotations) {
        if (annotations == null || annotations.isEmpty()) return false;
        for (AnnotationNode node : annotations) {
            if (node.desc.equals(blacklistDesc)) {
                return true;
            }
        }
        return false;
    }
}
