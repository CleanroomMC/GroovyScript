package com.cleanroommc.groovyscript.api;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public interface IGroovyCompat extends IGroovyContainer {

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
