package com.cleanroommc.groovyscript.compat.mods;

import com.cleanroommc.groovyscript.api.GroovyPlugin;
import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * This is used for external mod compat. Don't use this directly. Instead, implement {@link GroovyPlugin} on any class.
 * This class will then be automatically instanced.
 */
public class ExternalModContainer extends GroovyContainer<GroovyPropertyContainer> {

    private final GroovyPlugin groovyContainer;
    private final Supplier<GroovyPropertyContainer> container;
    private final String modId;
    private final String containerName;
    private final Collection<String> aliases;
    private final Priority priority;

    ExternalModContainer(@NotNull GroovyPlugin groovyContainer, @NotNull GroovyPropertyContainer container) {
        this.groovyContainer = Objects.requireNonNull(groovyContainer);
        Objects.requireNonNull(container);
        this.container = Suppliers.memoize(() -> {
            container.addPropertyFieldsOf(container, false);
            return container;
        });
        this.modId = groovyContainer.getModId();
        this.containerName = groovyContainer.getContainerName();
        Set<String> aliasSet = new ObjectOpenHashSet<>(groovyContainer.getAliases());
        aliasSet.add(modId);
        this.aliases = Collections.unmodifiableSet(aliasSet);
        this.priority = groovyContainer.getOverridePriority();
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

    @Override
    public @NotNull Collection<String> getAliases() {
        return aliases;
    }

    @Override
    public void onCompatLoaded(GroovyContainer<?> container) {
        groovyContainer.onCompatLoaded(container);
    }

    @Override
    public GroovyPropertyContainer get() {
        return container.get();
    }

    @Override
    public @NotNull GroovyPlugin.Priority getOverridePriority() {
        return priority;
    }
}
