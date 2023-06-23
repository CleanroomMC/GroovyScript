package com.cleanroommc.groovyscript.api;

import org.jetbrains.annotations.ApiStatus;

public interface IScriptReloadable {

    @GroovyBlacklist
    @ApiStatus.OverrideOnly
    void onReload();

    @GroovyBlacklist
    @ApiStatus.OverrideOnly
    void afterScriptLoad();

}
