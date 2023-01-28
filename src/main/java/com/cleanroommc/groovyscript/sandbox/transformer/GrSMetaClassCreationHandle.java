package com.cleanroommc.groovyscript.sandbox.transformer;

import com.cleanroommc.groovyscript.sandbox.interception.InterceptionManager;
import groovy.lang.Binding;
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
        if (!InterceptionManager.INSTANCE.isValid(theClass) && theClass != Binding.class) {
            return new BlackListedMetaClass(theClass);
        }
        return super.createNormalMetaClass(theClass, registry);
    }
}
