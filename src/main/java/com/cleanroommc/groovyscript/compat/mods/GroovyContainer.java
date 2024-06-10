package com.cleanroommc.groovyscript.compat.mods;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IGroovyContainer;
import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.api.IRegistrar;
import com.cleanroommc.groovyscript.mapper.ObjectMapper;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public abstract class GroovyContainer<T extends GroovyPropertyContainer> implements IGroovyContainer {

    public abstract T get();

    @Override
    public String toString() {
        return getContainerName();
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
    @GroovyBlacklist
    public IRegistrar getVirtualizedRegistrar() {
        return getRegistrar();
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
    @GroovyBlacklist
    public IRegistrar getRegistrar() {
        if (!isLoaded()) return null;
        T t = get();
        return t::addProperty;
    }

    public void addProperty(INamed property) {
        if (isLoaded()) {
            get().addProperty(property);
        }
    }

    public void addPropertiesOfFields(Object o, boolean privateToo) {
        if (isLoaded()) {
            get().addPropertyFieldsOf(o, privateToo);
        }
    }

    public <V> ObjectMapper.Builder<V> objectMapperBuilder(String name, Class<V> returnType) {
        return new ObjectMapper.Builder<>(name, returnType).mod(this);
    }
}
