package com.cleanroommc.groovyscript.api;

import org.jetbrains.annotations.Nullable;

/**
 * When this is implemented on a class, {@link #getProperty(String)} will be called when groovy tries to get a field from this class
 */
public interface IGroovyPropertyGetter {

    /**
     * Returns a property stored in this object. Usually stored in a map.
     *
     * @param name of the property
     * @return a property of any type or null if nothing found
     */
    @Nullable
    Object getProperty(String name);
}
