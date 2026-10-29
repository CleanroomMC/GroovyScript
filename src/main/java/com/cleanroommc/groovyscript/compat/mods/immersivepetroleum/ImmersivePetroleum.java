/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.compat.mods.immersivepetroleum;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;

public class ImmersivePetroleum extends GroovyPropertyContainer {

    public final Distillation distillation = new Distillation();
    public final Reservoir reservoir = new Reservoir();
}
