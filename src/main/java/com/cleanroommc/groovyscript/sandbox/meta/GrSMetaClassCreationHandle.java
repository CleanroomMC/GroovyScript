package com.cleanroommc.groovyscript.sandbox.meta;

import com.cleanroommc.groovyscript.sandbox.security.GroovySecurityManager;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassRegistry;
import groovy.lang.Script;

/**
 * Handles creation of groovy {@link MetaClass}s. It creates a special empty MetaClass for blacklisted classes.
 */
public class GrSMetaClassCreationHandle extends MetaClassRegistry.MetaClassCreationHandle {

    public static final GrSMetaClassCreationHandle INSTANCE = new GrSMetaClassCreationHandle();

    private GrSMetaClassCreationHandle() {
    }

    @Override
    protected MetaClass createNormalMetaClass(Class theClass, MetaClassRegistry registry) {
        if (!GroovySecurityManager.INSTANCE.isValid(theClass)) {
            return new BlackListedMetaClass(theClass);
        }
        if (theClass == Class.class) {
            return new ClassMetaClass(registry, theClass);
        }
        if (Script.class.isAssignableFrom(theClass)) {
            return new ScriptMetaClass(registry, theClass);
        }
        if (GroovyObject.class.isAssignableFrom(theClass)) {
            return new ClassScriptMetaClass(registry, theClass);
        }
        return super.createNormalMetaClass(theClass, registry);
    }

}
