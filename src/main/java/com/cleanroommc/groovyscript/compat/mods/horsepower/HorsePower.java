/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.compat.mods.horsepower;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;

public class HorsePower extends GroovyPropertyContainer {

    public final ChoppingBlock choppingBlock = new ChoppingBlock();
    public final ManualChoppingBlock manualChoppingBlock = new ManualChoppingBlock();
    public final Grindstone grindstone = new Grindstone();
    public final ManualGrindstone manualGrindstone = new ManualGrindstone();
    public final Press press = new Press();
}
