package com.cleanroommc.groovyscript.registry;

public interface IReloadableRegistry<T> {

    void onReload();

    void removeEntry(T t);
}
