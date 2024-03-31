package com.cleanroommc.groovyscript.compat.mods;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyPlugin;
import com.cleanroommc.groovyscript.api.IGroovyContainer;
import com.cleanroommc.groovyscript.api.IRegistrar;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public abstract class GroovyContainer<T extends ModPropertyContainer> implements IGroovyContainer {

    private final GroovyPlugin.Priority overridePriority;

    protected GroovyContainer(GroovyPlugin.Priority overridePriority) {
        this.overridePriority = overridePriority;
    }

    public abstract T get();

    @Override
    public String toString() {
        return getContainerName();
    }

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "1.1.0")
    @GroovyBlacklist
    public IRegistrar getVirtualizedRegistrar() {
        return getRegistrar();
    }

    @GroovyBlacklist
    public IRegistrar getRegistrar() {
        if (!isLoaded()) return null;
        T t = get();
        return t::addRegistry;
    }

    public GroovyPlugin.Priority getOverridePriority() {
        return overridePriority;
    }
}
