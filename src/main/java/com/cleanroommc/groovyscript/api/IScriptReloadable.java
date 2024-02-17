package com.cleanroommc.groovyscript.api;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

public interface IScriptReloadable {

    @GroovyBlacklist
    @ApiStatus.OverrideOnly
    void onReload();

    @GroovyBlacklist
    @ApiStatus.OverrideOnly
    void afterScriptLoad();

    Collection<String> getAliases();

    default String getName() {
        Collection<String> aliases = getAliases();
        if (aliases.isEmpty()) {
            return "EmptyName";
        }
        return aliases.iterator().next();
    }
}
