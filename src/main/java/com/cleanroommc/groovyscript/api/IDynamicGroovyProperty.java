package com.cleanroommc.groovyscript.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * When this is implemented on a class, {@link #getProperty(String)} will be called when groovy tries to get a field from this class
 */
@ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
@Deprecated
public interface IDynamicGroovyProperty {

    /**
     * Returns a property stored in this object. Usually stored in a map.
     *
     * @param name of the property
     * @return a property of any type or null if nothing found
     */
    @Nullable
    Object getProperty(String name);

    /**
     * Sets a property stored in this object to a new value.
     *
     * @param name  name of the property
     * @param value new property value
     * @return true if setting the property was successful
     */
    default boolean setProperty(String name, @Nullable Object value) {
        return false;
    }

    /**
     * Returns all properties stored in this object.
     *
     * @return all properties
     */
    Map<String, ?> getProperties();
}
