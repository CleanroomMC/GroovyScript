package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;

import java.util.Collections;
import java.util.List;

public interface IReloadableRegistry<T> {

    @GroovyBlacklist
    void onReload();

    void removeEntry(T t);

    // TODO: remove default
    default T findEntry(Object... data) {
        return null;
    }

    // TODO: remove default
    default List<T> findEntries(Object... data) {
        return Collections.emptyList();
    }

    @GroovyBlacklist
    default void afterScript() {
    }

}
