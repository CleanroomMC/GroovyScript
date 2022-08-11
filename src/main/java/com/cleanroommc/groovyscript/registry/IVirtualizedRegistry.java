package com.cleanroommc.groovyscript.registry;

import org.jetbrains.annotations.Nullable;

public interface IVirtualizedRegistry<T> {

    void add(T recipe);

    void remove(T recipe);

    @Nullable
    T find(Object... data);

}
