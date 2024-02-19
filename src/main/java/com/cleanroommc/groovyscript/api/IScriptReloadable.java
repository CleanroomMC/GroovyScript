package com.cleanroommc.groovyscript.api;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

public interface IScriptReloadable extends INamed {

    @GroovyBlacklist
    @ApiStatus.OverrideOnly
    void onReload();

    @GroovyBlacklist
    @ApiStatus.OverrideOnly
    void afterScriptLoad();
}
