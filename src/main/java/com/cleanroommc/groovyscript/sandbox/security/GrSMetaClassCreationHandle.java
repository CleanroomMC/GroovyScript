package com.cleanroommc.groovyscript.sandbox.security;

import groovy.lang.MetaClass;
import groovy.lang.MetaClassRegistry;

/**
 * Handles creation of groovy {@link MetaClass}s. It creates a special empty MetaClass for blacklisted classes
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
        return super.createNormalMetaClass(theClass, registry);
    }
}
