package com.cleanroommc.groovyscript.compat.mods;

import com.cleanroommc.groovyscript.api.GroovyPlugin;
import com.cleanroommc.groovyscript.api.IGroovyContainer;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class ExternalModContainer extends GroovyContainer<ModPropertyContainer> {

    private final IGroovyContainer groovyContainer;
    private final ModPropertyContainer container;
    private final String modId;
    private final String containerName;
    private final Collection<String> aliases;

    ExternalModContainer(@NotNull GroovyPlugin groovyContainer, @NotNull ModPropertyContainer container) {
        super(groovyContainer.getOverridePriority());
        this.groovyContainer = Objects.requireNonNull(groovyContainer);
        this.container = Objects.requireNonNull(container);
        this.modId = groovyContainer.getModId();
        this.containerName = groovyContainer.getContainerName();
        Set<String> aliasSet = new ObjectOpenHashSet<>(groovyContainer.getAliases());
        aliasSet.add(modId);
        this.aliases = Collections.unmodifiableSet(aliasSet);
    }

    @Override
    public @NotNull String getModId() {
        return modId;
    }

    @Override
    public @NotNull String getContainerName() {
        return containerName;
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    @NotNull
    @Override
    public Collection<String> getAliases() {
        return aliases;
    }

    @Override
    public void onCompatLoaded(GroovyContainer<?> container) {
        groovyContainer.onCompatLoaded(container);
    }

    @Override
    public ModPropertyContainer get() {
        return container;
    }
}
