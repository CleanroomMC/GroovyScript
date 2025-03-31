package com.cleanroommc.groovyscript.sandbox.transformer;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.sandbox.mapper.GroovyDeobfMapper;
import com.cleanroommc.groovyscript.sandbox.mapper.RemappedCachedField;
import com.cleanroommc.groovyscript.sandbox.mapper.RemappedCachedMethod;
import com.cleanroommc.groovyscript.sandbox.security.GroovySecurityManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.reflection.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.List;

/**
 * A helper class that filters blacklisted fields and methods and remaps their names if needed
 */
public class GroovyCodeFactory {

    public static final String MC_CLASS = "net.minecraft.";
    public static final boolean spongeForgeLoaded = Loader.isModLoaded("spongeforge");

    private GroovyCodeFactory() {}

    public static boolean shouldRemap(CachedClass cachedClass) {
        return !spongeForgeLoaded && !FMLLaunchHandler.isDeobfuscatedEnvironment() && cachedClass.getName().startsWith(MC_CLASS);
    }

    public static PrivilegedAction<CachedField[]> makeFieldsHook(CachedClass cachedClass) {
        return () -> {
            final boolean remap = shouldRemap(cachedClass);
            return Arrays.stream(cachedClass.getTheClass().getDeclaredFields())
                    .filter(f -> ReflectionUtils.checkCanSetAccessible(f, CachedClass.class))
                    .filter(GroovySecurityManager.INSTANCE::isValid)
                    .map(f -> makeField(cachedClass, f, remap))
                    .toArray(CachedField[]::new);
        };
    }

    private static CachedField makeField(CachedClass cachedClass, Field field, boolean tryRemap) {
        if (tryRemap) {
            String deobfName = GroovyDeobfMapper.getDeobfField(cachedClass.getTheClass(), field.getName());
            if (deobfName != null) {
                return new RemappedCachedField(field, deobfName);
            }
        }
        return new CachedField(field);
    }

    public static PrivilegedAction<CachedConstructor[]> makeConstructorsHook(CachedClass cachedClass) {
        return () -> Arrays.stream(cachedClass.getTheClass().getDeclaredConstructors())
                .filter(c -> !c.isSynthetic()) // GROOVY-9245: exclude inner class ctors
                .filter(c -> ReflectionUtils.checkCanSetAccessible(c, CachedClass.class))
                .filter(c -> !c.isAnnotationPresent(GroovyBlacklist.class))
                .map(c -> new CachedConstructor(cachedClass, c))
                .toArray(CachedConstructor[]::new);
    }

    public static PrivilegedAction<CachedMethod[]> makeMethodsHook(CachedClass cachedClass) {
        return () -> {
            try {
                final boolean remap = shouldRemap(cachedClass);
                return Arrays.stream(cachedClass.getTheClass().getDeclaredMethods())
                        .filter(m -> m.getName().indexOf('+') < 0) // no synthetic JDK 5+ methods
                        .filter(m -> ReflectionUtils.checkCanSetAccessible(m, CachedClass.class))
                        .filter(GroovySecurityManager.INSTANCE::isValid)
                        .map(m -> makeMethod(cachedClass, m, remap))
                        .distinct()
                        .toArray(CachedMethod[]::new);
            } catch (LinkageError e) {
                return CachedMethod.EMPTY_ARRAY;
            }
        };
    }

    private static CachedMethod makeMethod(CachedClass cachedClass, Method method, boolean tryRemap) {
        if (tryRemap) {
            String deobfName = GroovyDeobfMapper.getDeobfMethod(cachedClass.getTheClass(), method.getName());
            if (deobfName != null) {
                return new RemappedCachedMethod(cachedClass, method, deobfName);
            }
        }
        return new CachedMethod(cachedClass, method);
    }

    public static boolean inheritsMCClas(ClassNode classNode) {
        do {
            if (classNode.getName().startsWith(MC_CLASS)) {
                return true;
            }
            ClassNode[] interfaces = classNode.getInterfaces();
            if (interfaces != null) {
                for (ClassNode iface : interfaces) {
                    if (inheritsMCClas(iface)) {
                        return true;
                    }
                }
            }
            classNode = classNode.getSuperClass();
        } while (classNode != null && classNode != ClassHelper.OBJECT_TYPE);
        return false;
    }

    /**
     * This bad boy is responsible for remapping overriden methods. Called via Mixin
     */
    public static void remapOverrides(ClassNode classNode) {
        if (FMLLaunchHandler.isDeobfuscatedEnvironment()) return;
        if (!inheritsMCClas(classNode)) return;
        List<MethodNode> methodNodes = classNode.getMethods();
        for (int i = 0, methodNodesSize = methodNodes.size(); i < methodNodesSize; i++) {
            MethodNode methodNode = methodNodes.get(i);
            String obf = GroovyDeobfMapper.getObfuscatedMethodName(classNode, methodNode.getName(), methodNode.getParameters());
            if (obf != null) {
                classNode.addMethod(copyRemappedMethodNode(obf, methodNode));
            }
        }
    }

    /**
     * Copies a method node with a new name
     */
    private static MethodNode copyRemappedMethodNode(String name, MethodNode original) {
        MethodNode copy = new MethodNode(
                name,
                original.getModifiers(),
                original.getReturnType(),
                original.getParameters(),
                original.getExceptions(),
                original.getCode());
        copy.setAnnotationDefault(original.hasDefaultValue());
        copy.setColumnNumber(original.getColumnNumber());
        copy.setDeclaringClass(original.getDeclaringClass());
        copy.setGenericsTypes(original.getGenericsTypes());
        copy.setHasNoRealSourcePosition(original.hasNoRealSourcePosition());
        copy.setLastColumnNumber(original.getLastColumnNumber());
        copy.setLastLineNumber(original.getLastLineNumber());
        copy.setLineNumber(original.getLineNumber());
        copy.setMetaDataMap(original.getMetaDataMap());
        copy.setSynthetic(original.isSynthetic());
        copy.setSyntheticPublic(original.isSyntheticPublic());
        copy.setVariableScope(original.getVariableScope());
        return copy;
    }
}
