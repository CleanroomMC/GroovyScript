/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.compat.mods.naturesaura;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;

public class NaturesAura extends GroovyPropertyContainer {

    public final Altar altar = new Altar();
    public final Ritual ritual = new Ritual();
    public final Offering offering = new Offering();
    public final Spawning spawning = new Spawning();
}
