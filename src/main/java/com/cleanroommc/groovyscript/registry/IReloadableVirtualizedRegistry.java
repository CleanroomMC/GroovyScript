package com.cleanroommc.groovyscript.registry;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;

public interface IReloadableVirtualizedRegistry<T> extends IVirtualizedRegistry<T> {

    @GroovyBlacklist
    void onReload();

}
