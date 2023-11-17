package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

public interface IGroovyContainer {

    @NotNull
    String getModId();

    @NotNull
    String getModName();

    default boolean isLoaded() {
        return true;
    }

    @NotNull
    default Collection<String> getAliases() {
        return Collections.emptyList();
    }

    @Nullable
    @ApiStatus.OverrideOnly
    default ModPropertyContainer createModPropertyContainer() {
        return null;
    }

    @ApiStatus.OverrideOnly
    void onCompatLoaded(GroovyContainer<?> container, IGroovyCompatRegistryContainer registry);
}
