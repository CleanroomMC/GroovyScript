/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.compat.mods.integrateddynamics;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;

public class IntegratedDynamics extends GroovyPropertyContainer {

    public final DryingBasin dryingBasin = new DryingBasin();
    public final MechanicalDryingBasin mechanicalDryingBasin = new MechanicalDryingBasin();
    public final Squeezer squeezer = new Squeezer();
    public final MechanicalSqueezer mechanicalSqueezer = new MechanicalSqueezer();
}
