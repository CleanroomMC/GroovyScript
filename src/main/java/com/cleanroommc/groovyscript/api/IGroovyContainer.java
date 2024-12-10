package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
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
     * Scripts will be able to refer to the mods {@link com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer GroovyPropertyContainer}
     * with this id.
     *
     * @return the compat mod id
     */
    @NotNull
    String getModId();

    /**
     * Returns the name of this container. Is only used for logging and debugging.
     * It usually returns a mod name, but it doesn't have to.
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
    default @NotNull Collection<String> getAliases() {
        return Collections.singletonList(getModId());
    }

    /**
     * Called before scripts are executed for the first time. Called right before {@link GroovyPropertyContainer#initialize(GroovyContainer)}.
     * Used to initialize things like expansions with {@link com.cleanroommc.groovyscript.sandbox.expand.ExpansionHelper ExpansionHelper} and
     * object mappers with {@link com.cleanroommc.groovyscript.mapper.ObjectMapperManager ObjectMapperManager}.
     *
     * @param container the created container for the compat mod
     */
    @ApiStatus.OverrideOnly
    void onCompatLoaded(GroovyContainer<?> container);

    /**
     * Returns the override priority. Defines how this plugin should behave when another container with the same mod id exists.
     * The return value should be as low as possible. Internal container always return {@link Priority#NONE}.
     *
     * @return the override priority
     * @see Priority
     */
    default @NotNull Priority getOverridePriority() {
        return Priority.NONE;
    }

    enum Priority {
        /**
         * Default. Can be overridden by anything and can't override anything.
         */
        NONE,
        /**
         * Can override containers with priority NONE.
         */
        OVERRIDE,
        /**
         * Can override containers with priority NONE, OVERRIDE.
         */
        OVERRIDE_HIGH,
        /**
         * Can override containers with priority NONE, OVERRIDE, OVERRIDE_HIGH.
         */
        OVERRIDE_HIGHEST
    }
}
