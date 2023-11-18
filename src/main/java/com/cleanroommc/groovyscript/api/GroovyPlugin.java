package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Implement this on a class to add external mod compat with GroovyScript. GroovyScript will automatically find and instantiate the class.
 * A mod should have at most one class with this interface.
 * If you don't want the interface to be automatically instantiated, look into {@link Instance}.
 */
public interface GroovyPlugin extends IGroovyContainer {

    /**
     * Annotate a field in a class that implements {@link GroovyPlugin}.
     * Example: <pre>{@code
     *     public class ExamplePlugin implements GroovyPlugin {
     *
     *         @Instance
     *         private static ExamplePlugin instance;
     *         @Instance
     *         private static GroovyContainer<?> instance;
     *     }
     * }</pre>
     * When the interface is instantiated both fields will automatically be set via reflection.
     * The fields can be final, but your IDE might complain about that. <br>
     * You can initialise the {@code ExamplePlugin} field. Then the interface will not be instantiated, but the field will be used.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface Instance {
    }

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
}
