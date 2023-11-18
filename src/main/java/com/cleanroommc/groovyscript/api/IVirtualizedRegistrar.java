package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * A helper interface to register {@link VirtualizedRegistry VirtualizedRegistries} without having direct access to the
 * {@link com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer ModPropertyContainer}.
 * An instance can be obtained from {@link GroovyContainer#getVirtualizedRegistrar()}.
 */
@ApiStatus.NonExtendable
public interface IVirtualizedRegistrar {

    /**
     * Adds a {@link VirtualizedRegistry} with all its alias names.
     *
     * @param registry registry to add.
     */
    void addRegistry(VirtualizedRegistry<?> registry);

    /**
     * Finds all (static or not) fields for an object or a class that is a {@link VirtualizedRegistry} and adds it with
     * {@link #addRegistry(VirtualizedRegistry)}
     *
     * @param object object to add fields from
     */
    default void addFieldsOf(Object object) {
        boolean staticOnly = false;
        Class<?> clazz;
        if (object instanceof Class) {
            clazz = (Class<?>) object;
            staticOnly = true;
        } else {
            clazz = object.getClass();
        }
        for (Field field : clazz.getDeclaredFields()) {
            boolean isStatic = Modifier.isStatic(field.getModifiers());
            if (!field.isAnnotationPresent(GroovyBlacklist.class) && VirtualizedRegistry.class.isAssignableFrom(field.getType()) && (!staticOnly || isStatic)) {
                try {
                    Object o = field.get(isStatic ? null : object);
                    if (o != null) {
                        addRegistry((VirtualizedRegistry<?>) o);
                    }
                } catch (IllegalAccessException e) {
                    GroovyLog.get().errorMC("Failed to register {} as virtualized registry", field.getName());
                }
            }
        }
    }
}
