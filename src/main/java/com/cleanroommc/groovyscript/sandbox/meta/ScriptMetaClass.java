package com.cleanroommc.groovyscript.sandbox.meta;

import groovy.lang.MetaClassImpl;
import groovy.lang.MetaClassRegistry;

public class ScriptMetaClass extends MetaClassImpl {

    public ScriptMetaClass(MetaClassRegistry registry, Class theClass) {
        super(registry, theClass);
    }
}
