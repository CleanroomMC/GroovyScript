package com.cleanroommc.groovyscript.compat.mods;

import com.cleanroommc.groovyscript.api.IGroovyCompatRegistryContainer;
import com.cleanroommc.groovyscript.api.IGroovyContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class ExternalModContainer extends GroovyContainer<ModPropertyContainer> {

    private final IGroovyContainer groovyContainer;
    private final ModPropertyContainer container;
    private final String modId;
    private final String modName;
    private final Collection<String> aliases;

    public ExternalModContainer(IGroovyContainer groovyContainer, ModPropertyContainer container) {
        this.groovyContainer = groovyContainer;
        this.container = container;
        this.modId = groovyContainer.getModId();
        this.modName = groovyContainer.getModName();
        this.aliases = Collections.unmodifiableCollection(groovyContainer.getAliases());
        ModSupport.INSTANCE.registerContainer(this);
    }

    @Override
    public @NotNull String getModId() {
        return modId;
    }

    @Override
    public @NotNull String getModName() {
        return modName;
    }

    @NotNull
    @Override
    public Collection<String> getAliases() {
        return aliases;
    }

    @Override
    public void onCompatLoaded(GroovyContainer<?> container, IGroovyCompatRegistryContainer registry) {
        groovyContainer.onCompatLoaded(container, registry);
    }

    @Override
    public ModPropertyContainer get() {
        return container;
    }
}
