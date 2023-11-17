package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface IGroovyCompatRegistryContainer {

    void addRegistry(VirtualizedRegistry<?> registry);
}
