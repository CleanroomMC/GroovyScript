package com.cleanroommc.groovyscript.compat.mods;

import com.cleanroommc.groovyscript.api.IDynamicGroovyProperty;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public class ModPropertyContainer implements IDynamicGroovyProperty {

    private final Map<String, VirtualizedRegistry<?>> registries;

    public ModPropertyContainer() {
        this.registries = new Object2ObjectOpenHashMap<>();
    }

    protected void addRegistry(VirtualizedRegistry<?> registry) {
        for (String alias : registry.getAliases()) {
            this.registries.put(alias, registry);
        }
    }

    public Collection<VirtualizedRegistry<?>> getRegistries() {
        return registries.values();
    }

    @Override
    public @Nullable Object getProperty(String name) {
        return registries.get(name);
    }

}
