package com.cleanroommc.groovyscript.compat.mods;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IGroovyContainer;
import com.cleanroommc.groovyscript.api.IVirtualizedRegistrar;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public abstract class GroovyContainer<T extends ModPropertyContainer> implements IGroovyContainer {

    public abstract T get();

    @Deprecated
    public String getId() {
        return getModId();
    }

    @Override
    public String toString() {
        return getModName();
    }

    @GroovyBlacklist
    public IVirtualizedRegistrar getVirtualizedRegistrar() {
        if (!isLoaded()) return null;
        T t = get();
        return t::addRegistry;
    }
}
