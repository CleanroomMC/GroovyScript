/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.compat.mods.essentialcraft;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;

public class EssentialCraft extends GroovyPropertyContainer {

    public final DemonTradeManager demonTrade = new DemonTradeManager();
    public final MagicianTable magicianTable = new MagicianTable();
    public final MagmaticSmeltery magmaticSmeltery = new MagmaticSmeltery();
    public final MithrilineFurnace mithrilineFurnace = new MithrilineFurnace();
    public final RadiatingChamber radiatingChamber = new RadiatingChamber();
    public final WindRune windRune = new WindRune();
}
