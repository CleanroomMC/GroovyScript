package com.cleanroommc.groovyscript.compat.mods;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IGroovyContainer;
import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.api.IRegistrar;
import com.cleanroommc.groovyscript.mapper.ObjectMapper;
import org.jetbrains.annotations.ApiStatus;

/**
 * This is the base class for mod compat. It is created even if the other mod is not loaded.
 * For compat inside GroovyScript use {@link InternalModContainer}.
 * Otherwise, take a look at {@link com.cleanroommc.groovyscript.api.GroovyPlugin}.
 *
 * @param <T> type of the property container
 */
@ApiStatus.NonExtendable
public abstract class GroovyContainer<T extends GroovyPropertyContainer> implements IGroovyContainer {

    public abstract T get();

    @Override
    public String toString() {
        return getContainerName();
    }

    /**
     * @deprecated Use {@link #addProperty(INamed)} and {@link #addPropertiesOfFields(Object, boolean)} from this class instead.
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
    @GroovyBlacklist
    public IRegistrar getVirtualizedRegistrar() {
        return getRegistrar();
    }

    /**
     * @deprecated Use {@link #addProperty(INamed)} and {@link #addPropertiesOfFields(Object, boolean)} from this class instead.
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
    @GroovyBlacklist
    public IRegistrar getRegistrar() {
        if (!isLoaded()) return null;
        T t = get();
        return t::addProperty;
    }

    /**
     * Adds a property which can be accessed like a field from groovy.
     *
     * @param property the property to add
     */
    public void addProperty(INamed property) {
        if (isLoaded()) {
            get().addProperty(property);
        }
    }

    /**
     * Finds all fields in a class which type is an instance of {@link INamed} and adds it as a property.
     * If the given object is a class only static variables are used.
     *
     * @param o          object to find fields in
     * @param privateToo true if private fields should be used too
     */
    public void addPropertiesOfFields(Object o, boolean privateToo) {
        if (isLoaded()) {
            get().addPropertyFieldsOf(o, privateToo);
        }
    }

    /**
     * Creates an object mapper builder.
     *
     * @param name       the function name
     * @param returnType the return type
     * @param <V>        the return type
     * @return a new object mapper builder
     */
    public <V> ObjectMapper.Builder<V> objectMapperBuilder(String name, Class<V> returnType) {
        return new ObjectMapper.Builder<>(name, returnType).mod(this);
    }
}
