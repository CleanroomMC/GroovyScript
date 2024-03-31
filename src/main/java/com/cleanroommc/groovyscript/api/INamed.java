package com.cleanroommc.groovyscript.api;

import java.util.Collection;

public interface INamed {

    Collection<String> getAliases();

    default String getName() {
        Collection<String> aliases = getAliases();
        if (aliases.isEmpty()) {
            return "EmptyName";
        }
        return aliases.iterator().next();
    }

    @GroovyBlacklist
    default boolean isEnabled() {
        return true;
    }

}
