/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

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
