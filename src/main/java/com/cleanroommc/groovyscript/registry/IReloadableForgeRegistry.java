package com.cleanroommc.groovyscript.registry;

import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;

public interface IReloadableForgeRegistry<V extends IForgeRegistryEntry<V>> {

    void onReload();

    void removeEntry(V dummy);

    @Unmodifiable
    Collection<V> getReloadableEntries();
}
