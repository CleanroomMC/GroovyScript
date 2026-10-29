/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.event;

public enum EventBusType {

    MAIN,
    FORGE, // Unused right now, priming for @EventHandler
    ORE_GENERATION,
    TERRAIN_GENERATION

}
