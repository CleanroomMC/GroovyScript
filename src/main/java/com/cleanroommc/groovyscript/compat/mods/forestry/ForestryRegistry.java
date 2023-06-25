package com.cleanroommc.groovyscript.compat.mods.forestry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import forestry.api.core.ForestryAPI;
import forestry.modules.ForestryModuleUids;
import org.jetbrains.annotations.ApiStatus;

public abstract class ForestryRegistry<T> extends VirtualizedRegistry<T> {

    public ForestryRegistry(String... aliases) {
        super(aliases);
    }

    @GroovyBlacklist
    @ApiStatus.Internal
    public boolean isEnabled() {
        return ForestryAPI.moduleManager.isModuleEnabled("forestry", ForestryModuleUids.FACTORY);
    }
}
