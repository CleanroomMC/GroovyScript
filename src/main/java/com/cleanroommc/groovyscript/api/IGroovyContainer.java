package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

/**
 * A GroovyScript compat container. Each mod compat has one of these.
 * For internal mod compat see {@link com.cleanroommc.groovyscript.compat.mods.ModSupport ModSupport} and
 * {@link com.cleanroommc.groovyscript.compat.mods.InternalModContainer InternalModContainer}. For external compat refer to {@link GroovyPlugin}.
 */
public interface IGroovyContainer {

    /**
     * Returns the mod id of the compat mod. This will be used to check if the mod is loaded.
     * Scripts will be able to refer to the mods {@link com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer ModPropertyContainer}
     * with this id.
     *
     * @return the compat mod id
     */
    @NotNull
    String getModId();

    /**
     * Returns the name of this container. Is only used for logging and debugging.
     *
     * @return the name of the container
     */
    @NotNull
    String getContainerName();

    /**
     * @return true if the compat mod is currently loaded.
     */
    boolean isLoaded();

    /**
     * Returns aliases of the compat mod. These are all the variable name scripts can use to refer to this mod.
     * By default, it's only the mod id from {@link #getModId()}.
     *
     * @return aliases
     */
    @NotNull
    default Collection<String> getAliases() {
        return Collections.singletonList(getModId());
    }

    /**
     * Called before scripts are executed for the first time. Called right before {@link ModPropertyContainer#initialize()}.
     * Used to initialize things like expansions with {@link com.cleanroommc.groovyscript.sandbox.expand.ExpansionHelper ExpansionHelper} and
     * game object handlers with {@link com.cleanroommc.groovyscript.gameobjects.GameObjectHandlerManager GameObjectHandlerManager}.
     *
     * @param container the created container for the compat mod
     */
    @ApiStatus.OverrideOnly
    void onCompatLoaded(GroovyContainer<?> container);
}
