package com.cleanroommc.groovyscript.sandbox;

import groovy.lang.MetaClassImpl;

public class JavaBeanException extends RuntimeException {

    public final Throwable parent;
    public final MetaClassImpl metaClass;

    public JavaBeanException(Throwable parent, MetaClassImpl metaClass) {
        super("An error occurred while trying to gather java properties for class " + metaClass.getTheClass().getName());
        this.parent = parent;
        this.metaClass = metaClass;
    }
}
