package com.cleanroommc.groovyscript.compat.mods;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyPlugin;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraftforge.fml.common.Loader;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Will not be removed but made private and renamed to InternalContainer
 */
@SuppressWarnings("all")
public class InternalModContainer<T extends ModPropertyContainer> extends GroovyContainer<T> {

    private final String modId, containerName;
    private final Supplier<T> modProperty;
    private final boolean loaded;
    private final Collection<String> aliases;

    InternalModContainer(String modId, String containerName, @NotNull Supplier<T> modProperty) {
        this(modId, containerName, modProperty, new String[0]);
    }

    InternalModContainer(String modId, String containerName, @NotNull Supplier<T> modProperty, String... aliases) {
        super(GroovyPlugin.Priority.NONE);
        if (ModSupport.isFrozen()) {
            throw new RuntimeException("Groovy mod containers must be registered at construction event! Tried to register '" + containerName + "' too late.");
        }
        if (ModSupport.INSTANCE.hasCompatFor(modId)) {
            GroovyScript.LOGGER.error("Error while trying to add internal compat!");
            GroovyScript.LOGGER.error("Internal mod compat must be added from GroovyScript before any other compat, but");
            throw new IllegalStateException("compat was already added for " + modId + "!");
        }
        this.modId = modId;
        this.containerName = containerName;
        this.modProperty = Suppliers.memoize(modProperty);
        this.loaded = Loader.isModLoaded(modId);
        Set<String> aliasSet = new ObjectOpenHashSet<>(aliases);
        aliasSet.add(modId);
        this.aliases = Collections.unmodifiableSet(aliasSet);
        ModSupport.INSTANCE.registerContainer(this);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @NotNull
    @Override
    public Collection<String> getAliases() {
        return aliases;
    }

    @Override
    public T get() {
        return modProperty != null && isLoaded() ? modProperty.get() : null;
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
    public void onCompatLoaded(GroovyContainer<?> container) {
    }
}
