package com.cleanroommc.groovyscript.compat.mods;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import net.minecraftforge.fml.common.Loader;
import org.jetbrains.annotations.NotNull;

/**
 * Will not be removed but made private and renamed to InternalContainer
 */
@SuppressWarnings("all")
class InternalModContainer<T extends ModPropertyContainer> extends GroovyContainer<T> {

    private final String modId, modName;
    private final Supplier<T> modProperty;
    private final boolean loaded;

    public InternalModContainer(String modId, String modName, @NotNull Supplier<T> modProperty) {
        this(modId, modName, modProperty, new String[0]);
    }

    public InternalModContainer(String modId, String modName, @NotNull Supplier<T> modProperty, String... aliases) {
        if (ModSupport.isFrozen()) {
            throw new RuntimeException("Groovy mod containers must be registered at construction event! Tried to register '" + modName + "' too late.");
        }
        if (ModSupport.INSTANCE.hasCompatFor(modId)) {
            throw new IllegalStateException("Compat was already added for " + modId + "!");
        }
        this.modId = modId;
        this.modName = modName;
        this.modProperty = Suppliers.memoize(modProperty);
        this.loaded = Loader.isModLoaded(modId);
        ModSupport.INSTANCE.registerContainer(this);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
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
    public @NotNull String getModName() {
        return modName;
    }

    @Override
    public void onCompatLoaded(GroovyContainer<?> container) {
    }
}
