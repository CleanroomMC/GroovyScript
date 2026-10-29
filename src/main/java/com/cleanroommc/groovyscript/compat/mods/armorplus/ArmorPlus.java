/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.compat.mods.armorplus;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;

public class ArmorPlus extends GroovyPropertyContainer {

    public final ChampionBench championBench = new ChampionBench();
    public final HighTechBench highTechBench = new HighTechBench();
    public final LavaInfuser lavaInfuser = new LavaInfuser();
    public final UltimateBench ultimateBench = new UltimateBench();
    public final WorkBench workBench = new WorkBench();
}
