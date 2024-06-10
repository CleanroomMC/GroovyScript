package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
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
    default @Nullable GroovyPropertyContainer createGroovyPropertyContainer() {
        return createModPropertyContainer();
    }

    /**
     * @deprecated use {@link #createGroovyPropertyContainer()} instead
     */
    @GroovyBlacklist
    @ApiStatus.OverrideOnly
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "1.2.0")
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
}
