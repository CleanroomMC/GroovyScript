package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @deprecated The methods of this class have been added directly to {@link GroovyContainer}
 */
@ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
@Deprecated
@ApiStatus.NonExtendable
public interface IRegistrar {

    /**
     * Adds a {@link INamed} with all its alias names.
     *
     * @param registry registry to add.
     */
    @Deprecated
    void addRegistry(INamed registry);

    /**
     * Finds all (static or not) fields for an object or a class that is a {@link INamed} and adds it with
     * {@link #addRegistry(INamed)}
     *
     * @param object object to add fields from
     */
    @Deprecated
    default void addFieldsOf(Object object) {
        boolean staticOnly = false;
        Class<?> clazz;
        if (object instanceof Class<?> c) {
            clazz = c;
            staticOnly = true;
        } else {
            clazz = object.getClass();
        }
        for (Field field : clazz.getDeclaredFields()) {
            boolean isStatic = Modifier.isStatic(field.getModifiers());
            if (!field.isAnnotationPresent(GroovyBlacklist.class) &&
                INamed.class.isAssignableFrom(field.getType()) &&
                (!staticOnly || isStatic) &&
                field.isAccessible()) {
                try {
                    Object o = field.get(isStatic ? null : object);
                    if (o != null) {
                        addRegistry((INamed) o);
                    }
                } catch (IllegalAccessException e) {
                    GroovyLog.get().errorMC("Failed to register {} as virtualized registry", field.getName());
                }
            }
        }
    }
}
