package com.cleanroommc.groovyscript.sandbox.meta;

import com.cleanroommc.groovyscript.GroovyScript;
import groovy.lang.MetaClassRegistry;

public class ClassScriptMetaClass extends ClassMetaClass {

    public ClassScriptMetaClass(MetaClassRegistry registry, Class theClass) {
        super(registry, theClass);
    }

    @Override
    public Object invokeMissingProperty(Object instance, String propertyName, Object optionalValue, boolean isGetter) {
        Object o = GroovyScript.getSandbox().getBindings().get(propertyName);
        if (o != null) {
            return isGetter ? o : null;
        }
        return super.invokeMissingProperty(instance, propertyName, optionalValue, isGetter);
    }

}
