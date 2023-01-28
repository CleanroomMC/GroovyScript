package com.cleanroommc.groovyscript.sandbox.mapper;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.CachedField;

import java.lang.reflect.Field;
import java.security.PrivilegedAction;
import java.util.Arrays;

import static org.codehaus.groovy.reflection.ReflectionUtils.checkCanSetAccessible;

public class RemappedCachedField extends CachedField {

    private static final String MC_CLASS = "net.minecraft.";

    private final String deobfName;

    public RemappedCachedField(Field field, String deobfName) {
        super(field);
        this.deobfName = deobfName;
    }

    @Override
    public String getName() {
        return deobfName;
    }

    public static PrivilegedAction<CachedField[]> makeFieldsHook(CachedClass cachedClass) {
        return () -> Arrays.stream(cachedClass.getTheClass().getDeclaredFields())
                .filter(f -> checkCanSetAccessible(f, CachedClass.class))
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
}
