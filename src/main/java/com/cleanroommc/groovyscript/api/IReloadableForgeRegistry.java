package com.cleanroommc.groovyscript.api;

import net.minecraft.util.ResourceLocation;

/**
 * Access interface for reloadable forge registries
 */
public interface IReloadableForgeRegistry<V> {

    V registerEntry(V registryEntry);

    void removeEntry(ResourceLocation name);

    void onReload();
}
