package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.sandbox.interception.InterceptionManager;
import com.cleanroommc.groovyscript.sandbox.mapper.GroovyDeobfMapper;
import com.cleanroommc.groovyscript.sandbox.mapper.RemappedCachedField;
import com.cleanroommc.groovyscript.sandbox.mapper.RemappedCachedMethod;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.codehaus.groovy.reflection.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.PrivilegedAction;
import java.util.Arrays;

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
                .filter(InterceptionManager.INSTANCE::isValid)
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
                        .filter(m -> {
                            boolean b = InterceptionManager.INSTANCE.isValid(m);
                            if (!b)
                                GroovyLog.get().infoMC("Method {} in {} is invalid", m.getName(), m.getDeclaringClass());
                            return b;
                        })
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
}
