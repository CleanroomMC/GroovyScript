package com.cleanroommc.groovyscript.api;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.ApiStatus;

/**
 * Access interface for reloadable forge registries
 */
public interface IReloadableForgeRegistry<V extends IForgeRegistryEntry<V>> {

    V groovyScript$registerEntry(V registryEntry);

    void groovyScript$removeEntry(ResourceLocation name);

    void groovyScript$onReload();

    @ApiStatus.Internal
    void groovyScript$putDummy(V dummy, V realEntry, ResourceLocation name, int id, Object owner);

    @ApiStatus.Internal
    void groovyScript$forceAdd(V entry, int id, Object owner);

    default boolean groovyScript$isDummy() {
        return false;
    }

    enum DummyContext {
        REMOVAL, ADDITION, RELOADING
    }

    class DummyRFG<V extends IForgeRegistryEntry<V>> implements IReloadableForgeRegistry<V> {

        @Override
        public V groovyScript$registerEntry(V registryEntry) {
            return null;
        }

        @Override
        public void groovyScript$removeEntry(ResourceLocation name) {}

        @Override
        public void groovyScript$onReload() {}

        @Override
        public void groovyScript$putDummy(V dummy, V realEntry, ResourceLocation name, int id, Object owner) {}

        @Override
        public void groovyScript$forceAdd(V entry, int id, Object owner) {}

        @Override
        public boolean groovyScript$isDummy() {
            return true;
        }
    }
}
