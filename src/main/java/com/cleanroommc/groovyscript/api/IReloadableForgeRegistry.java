package com.cleanroommc.groovyscript.api;

import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Access interface for reloadable forge registries
 */
public interface IReloadableForgeRegistry<V> {

    V registerEntry(V registryEntry);

    void removeEntry(ResourceLocation name, @Nullable V dummy);

    void onReload();
}
