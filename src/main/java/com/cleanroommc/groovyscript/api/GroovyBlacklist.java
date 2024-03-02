package com.cleanroommc.groovyscript.api;

import java.lang.annotation.*;

/**
 * Use this annotation on fields, methods or classes to make them inaccessible for groovy.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
public @interface GroovyBlacklist {

    /**
     * By default, methods inherit this annotation from super classes.
     * By setting this value to true, the annotated element will be removed from the blacklist.
     *
     * @return true if this method should not be blacklisted when super class method has this annotation
     */
    boolean removeFromBlacklist() default false;
}
