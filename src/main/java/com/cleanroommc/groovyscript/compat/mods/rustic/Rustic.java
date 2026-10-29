/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.compat.mods.rustic;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;

public class Rustic extends GroovyPropertyContainer {

    public final Alchemy alchemy = new Alchemy();
    public final BrewingBarrel brewingBarrel = new BrewingBarrel();
    public final CrushingTub crushingTub = new CrushingTub();
    public final EvaporatingBasin evaporatingBasin = new EvaporatingBasin();
}
