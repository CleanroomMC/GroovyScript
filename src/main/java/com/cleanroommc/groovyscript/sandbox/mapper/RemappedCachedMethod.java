package com.cleanroommc.groovyscript.sandbox.mapper;

import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.CachedMethod;

import java.lang.reflect.Method;

public class RemappedCachedMethod extends CachedMethod {

    private final String deobfName;

    public RemappedCachedMethod(CachedClass clazz, Method method, String deobfName) {
        super(clazz, method);
        this.deobfName = deobfName;
    }

    @Override
    public String getName() {
        return deobfName;
    }
}
