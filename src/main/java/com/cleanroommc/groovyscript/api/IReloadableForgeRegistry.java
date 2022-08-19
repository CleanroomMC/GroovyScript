package com.cleanroommc.groovyscript.api;

import net.minecraft.util.ResourceLocation;

public interface IReloadableForgeRegistry<V> {

    V registerEntry(V registryEntry);

    void removeEntry(ResourceLocation name);

    void onReload();

}
