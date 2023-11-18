package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface GroovyPlugin extends IGroovyContainer {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @interface Instance {
    }

    @GroovyBlacklist
    @ApiStatus.OverrideOnly
    default @Nullable ModPropertyContainer createModPropertyContainer() {
        return null;
    }

    @Override
    @ApiStatus.NonExtendable
    default boolean isLoaded() {
        return true;
    }
}
