package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public interface IGroovyContainer {

    @NotNull
    String getModId();

    @NotNull
    String getModName();

    boolean isLoaded();

    @NotNull
    default Collection<String> getAliases() {
        return Collections.emptyList();
    }

    @ApiStatus.OverrideOnly
    void onCompatLoaded(GroovyContainer<?> container);
}
