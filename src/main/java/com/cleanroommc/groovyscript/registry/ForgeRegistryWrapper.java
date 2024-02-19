package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IReloadableForgeRegistry;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Collection;
import java.util.Collections;

public class ForgeRegistryWrapper<T extends IForgeRegistryEntry<T>> implements IScriptReloadable {

    private final IForgeRegistry<T> registry;
    private final Collection<String> aliases;

    public ForgeRegistryWrapper(IForgeRegistry<T> registry, Collection<String> aliases) {
        this.registry = registry;
        this.aliases = Collections.unmodifiableCollection(aliases);
    }

    @GroovyBlacklist
    public IForgeRegistry<T> getRegistry() {
        return registry;
    }

    @GroovyBlacklist
    @Override
    public final void onReload() {
        ((IReloadableForgeRegistry<?>) registry).groovyScript$onReload();
    }

    @GroovyBlacklist
    @Override
    public void afterScriptLoad() {}

    @Override
    public Collection<String> getAliases() {
        return aliases;
    }
}
