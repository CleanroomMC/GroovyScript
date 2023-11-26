package com.cleanroommc.groovyscript.api;

import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

/**
 * Access interface for reloadable forge registries
 */
public interface IReloadableForgeRegistry<V> {

    V registerEntry(V registryEntry);

    void removeEntry(ResourceLocation name);

    void onReload();

    @ApiStatus.Internal
    void groovyscript$putDummy(V dummy, V realEntry, ResourceLocation name, int id, Object owner);

    @ApiStatus.Internal
    void groovyscript$forceAdd(V entry, int id, Object owner);
}
