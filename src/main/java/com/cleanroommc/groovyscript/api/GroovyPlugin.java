package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implement this on a class to add external mod compat with GroovyScript. GroovyScript will automatically find and instantiate the class.
 * A mod should have at most one class with this interface.
 */
public interface GroovyPlugin extends IGroovyContainer {

    /**
     * Creates the mod property container for this mod. If this method returns null a default container will be created.
     *
     * @return a new mod property container
     */
    @GroovyBlacklist
    @ApiStatus.OverrideOnly
    default @Nullable ModPropertyContainer createModPropertyContainer() {
        return null;
    }

    /**
     * This method exist because of the extended interface. It has no use in this interface.
     */
    @Override
    @ApiStatus.NonExtendable
    default boolean isLoaded() {
        return true;
    }

    /**
     * Returns the override priority. Defines how this plugin should behave when another container with the same mod id exists.
     * The return value should be as low as possible. Internal container always return {@link Priority#NONE}.
     * @return the override priority
     * @see Priority
     */
    @NotNull
    default Priority getOverridePriority() {
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
