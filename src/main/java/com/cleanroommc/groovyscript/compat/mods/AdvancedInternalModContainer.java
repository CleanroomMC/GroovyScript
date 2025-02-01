package com.cleanroommc.groovyscript.compat.mods;

import com.google.common.base.Supplier;
import org.jetbrains.annotations.NotNull;

public class AdvancedInternalModContainer<T extends AdvancedGroovyPropertyContainer> extends InternalModContainer<T> {

    AdvancedInternalModContainer(String modId, String containerName, @NotNull Supplier<T> modProperty) {
        super(modId, containerName, modProperty);
    }

    AdvancedInternalModContainer(String modId, String containerName, @NotNull Supplier<T> modProperty, String... aliases) {
        super(modId, containerName, modProperty, aliases);
    }

    @Override
    public void onCompatLoaded(GroovyContainer<?> container) {
        super.onCompatLoaded(container);
        this.get().onCompatLoaded();
    }
}
