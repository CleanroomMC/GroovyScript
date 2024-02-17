package com.cleanroommc.groovyscript.compat.mods;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IDynamicGroovyProperty;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.api.IVirtualizedRegistrar;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

public class ModPropertyContainer implements IDynamicGroovyProperty {

    private final Map<String, IScriptReloadable> registries;

    public ModPropertyContainer() {
        this.registries = new Object2ObjectOpenHashMap<>();
        ((IVirtualizedRegistrar) this::addRegistry).addFieldsOf(this);
    }

    protected void addRegistry(IScriptReloadable registry) {
        for (String alias : registry.getAliases()) {
            this.registries.put(alias, registry);
        }
    }

    public Collection<IScriptReloadable> getRegistries() {
        return registries.values();
    }

    @Override
    public @Nullable Object getProperty(String name) {
        return registries.get(name);
    }

    /**
     * Register bracket handlers, bindings, expansions etc. here
     */
    @GroovyBlacklist
    @ApiStatus.OverrideOnly
    public void initialize() {
    }
}
