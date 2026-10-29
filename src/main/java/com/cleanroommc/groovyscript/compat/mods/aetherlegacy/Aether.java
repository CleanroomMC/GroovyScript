/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.compat.mods.aetherlegacy;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;

public class Aether extends GroovyPropertyContainer {

    public final Enchanter enchanter = new Enchanter();
    public final EnchanterFuel enchanterFuel = new EnchanterFuel();
    public final Freezer freezer = new Freezer();
    public final FreezerFuel freezerFuel = new FreezerFuel();
    public final Accessory accessory = new Accessory();
}
