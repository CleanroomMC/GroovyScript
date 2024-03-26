package com.cleanroommc.groovyscript.compat.mods;

import com.cleanroommc.groovyscript.api.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class ModPropertyContainer implements IDynamicGroovyProperty {

    private final Map<String, INamed> registries;

    public ModPropertyContainer() {
        this.registries = new Object2ObjectOpenHashMap<>();
        ((IRegistrar) this::addRegistry).addFieldsOf(this);
    }

    protected void addRegistry(INamed registry) {
        for (String alias : registry.getAliases()) {
            this.registries.put(alias, registry);
        }
    }

    public Collection<INamed> getRegistries() {
        return registries.values();
    }

    @Override
    public @Nullable Object getProperty(String name) {
        INamed registry = registries.get(name);
        if (registry == null) {
            GroovyLog.get().error("Attempted to access registry {}, but could not find a registry with that name", name);
            return null;
        }
        if (!registry.isEnabled()) {
            GroovyLog.get().error("Attempted to access registry {}, but that registry was disabled", registry.getName());
            return null;
        }
        return registry;
    }

    @Override
    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(this.registries);
    }

    /**
     * Register bracket handlers, bindings, expansions etc. here
     */
    @GroovyBlacklist
    @ApiStatus.OverrideOnly
    public void initialize() {
    }
}
