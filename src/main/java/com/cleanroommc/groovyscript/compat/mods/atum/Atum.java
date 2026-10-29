/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.compat.mods.atum;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;

public class Atum extends GroovyPropertyContainer {

    public final Kiln kiln = new Kiln();
    public final Quern quern = new Quern();
    public final SpinningWheel spinningWheel = new SpinningWheel();
}
