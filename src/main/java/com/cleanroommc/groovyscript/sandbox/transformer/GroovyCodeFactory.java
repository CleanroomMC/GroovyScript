package com.cleanroommc.groovyscript.sandbox.transformer;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.sandbox.mapper.GroovyDeobfMapper;
import com.cleanroommc.groovyscript.sandbox.mapper.RemappedCachedField;
import com.cleanroommc.groovyscript.sandbox.mapper.RemappedCachedMethod;
import com.cleanroommc.groovyscript.sandbox.security.GroovySecurityManager;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
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

    private GroovyCodeFactory() {
    }

    public static PrivilegedAction<CachedField[]> makeFieldsHook(CachedClass cachedClass) {
        return () -> Arrays.stream(cachedClass.getTheClass().getDeclaredFields())
                .filter(f -> ReflectionUtils.checkCanSetAccessible(f, CachedClass.class))
                .filter(GroovySecurityManager.INSTANCE::isValid)
                .map(!FMLLaunchHandler.isDeobfuscatedEnvironment() && cachedClass.getName().startsWith(MC_CLASS) ?
                     f -> makeField(cachedClass, f) :
                     CachedField::new)
                .toArray(CachedField[]::new);
    }

    private static CachedField makeField(CachedClass cachedClass, Field field) {
        String deobfName = GroovyDeobfMapper.getDeobfField(cachedClass.getTheClass(), field.getName());
        return deobfName == null ?
               new CachedField(field) :
               new RemappedCachedField(field, deobfName);
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
                return Arrays.stream(cachedClass.getTheClass().getDeclaredMethods())
                        .filter(m -> m.getName().indexOf('+') < 0) // no synthetic JDK 5+ methods
                        .filter(m -> ReflectionUtils.checkCanSetAccessible(m, CachedClass.class))
                        .filter(GroovySecurityManager.INSTANCE::isValid)
                        .map(!FMLLaunchHandler.isDeobfuscatedEnvironment() && cachedClass.getName().startsWith(MC_CLASS) ?
                             m -> makeMethod(cachedClass, m) :
                             m -> new CachedMethod(cachedClass, m))
                        .toArray(CachedMethod[]::new);
            } catch (LinkageError e) {
                return CachedMethod.EMPTY_ARRAY;
            }
        };
    }

    private static CachedMethod makeMethod(CachedClass cachedClass, Method method) {
        String deobfName = GroovyDeobfMapper.getDeobfMethod(cachedClass.getTheClass(), method.getName());
        return deobfName == null ?
               new CachedMethod(cachedClass, method) :
               new RemappedCachedMethod(cachedClass, method, deobfName);
    }

    /**
     * This bad boy is responsible for remapping overriden methods.
     * Called via Mixin
     */
    public static void remapOverrides(ClassNode classNode) {
        if (FMLLaunchHandler.isDeobfuscatedEnvironment()) return;
        ClassNode superClass = classNode.getSuperClass();
        if (superClass == null || !superClass.getName().startsWith(MC_CLASS)) return;
        List<MethodNode> methodNodes = classNode.getMethods();
        for (int i = 0, methodNodesSize = methodNodes.size(); i < methodNodesSize; i++) {
            MethodNode methodNode = methodNodes.get(i);
            String obf = GroovyDeobfMapper.getObfuscatedMethodName(superClass, methodNode.getName(), methodNode.getParameters());
            if (obf != null) {
                classNode.addMethod(copyRemappedMethodNode(obf, methodNode));
            }
        }
    }

    /**
     * Copies a method node with a new name
     */
    private static MethodNode copyRemappedMethodNode(String name, MethodNode original) {
        MethodNode copy = new MethodNode(name, original.getModifiers(), original.getReturnType(), original.getParameters(), original.getExceptions(), original.getCode());
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
