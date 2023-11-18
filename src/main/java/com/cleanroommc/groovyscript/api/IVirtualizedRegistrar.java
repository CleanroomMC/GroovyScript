package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@ApiStatus.NonExtendable
public interface IVirtualizedRegistrar {

    void addRegistry(VirtualizedRegistry<?> registry);

    default void addFieldsOf(Object object) {
        for (Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(GroovyBlacklist.class) && VirtualizedRegistry.class.isAssignableFrom(field.getType())) {
                try {
                    addRegistry((VirtualizedRegistry<?>) field.get(Modifier.isStatic(field.getModifiers()) ? null : object));
                } catch (IllegalAccessException e) {
                    GroovyLog.get().errorMC("Failed to register {} as virtualized registry", field.getName());
                }
            }
        }
    }
}
