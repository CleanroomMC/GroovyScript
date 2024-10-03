package com.cleanroommc.groovyscript.sandbox.mapper;

import org.codehaus.groovy.reflection.CachedField;

import java.lang.reflect.Field;

public class RemappedCachedField extends CachedField {

    private final String deobfName;

    public RemappedCachedField(Field field, String deobfName) {
        super(field);
        this.deobfName = deobfName;
    }

    @Override
    public String getName() {
        return deobfName;
    }

}
