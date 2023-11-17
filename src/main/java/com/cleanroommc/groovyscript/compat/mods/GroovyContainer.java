package com.cleanroommc.groovyscript.compat.mods;

import com.cleanroommc.groovyscript.api.IGroovyContainer;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public abstract class GroovyContainer<T extends ModPropertyContainer> implements IGroovyContainer {

    public abstract T get();

    @Deprecated
    public String getId() {
        return getModId();
    }

    @Override
    public String toString() {
        return getModName();
    }
}
