/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.api;

import org.jetbrains.annotations.ApiStatus;

public interface IScriptReloadable extends INamed {

    @GroovyBlacklist
    @ApiStatus.OverrideOnly
    void onReload();

    @GroovyBlacklist
    @ApiStatus.OverrideOnly
    void afterScriptLoad();
}
