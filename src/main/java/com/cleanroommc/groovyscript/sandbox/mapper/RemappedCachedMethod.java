package com.cleanroommc.groovyscript.sandbox.mapper;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.CachedMethod;

import java.lang.reflect.Method;
import java.security.PrivilegedAction;
import java.util.Arrays;

import static org.codehaus.groovy.reflection.ReflectionUtils.checkCanSetAccessible;

public class RemappedCachedMethod extends CachedMethod {

    private static final String MC_CLASS = "net.minecraft.";

    private final String deobfName;

    public RemappedCachedMethod(CachedClass clazz, Method method, String deobfName) {
        super(clazz, method);
        this.deobfName = deobfName;
    }

    @Override
    public String getName() {
        return deobfName;
    }

    public static PrivilegedAction<CachedMethod[]> makeMethodsHook(CachedClass cachedClass) {
        return () -> {
            try {
                return Arrays.stream(cachedClass.getTheClass().getDeclaredMethods())
                        // skip synthetic methods inserted by JDK 1.5+ compilers
                        .filter(m -> !m.isBridge() && m.getName().indexOf('+') < 0)
                        .filter(m -> checkCanSetAccessible(m, CachedClass.class))
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
