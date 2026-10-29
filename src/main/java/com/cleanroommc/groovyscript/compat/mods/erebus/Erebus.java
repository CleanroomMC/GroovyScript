/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.compat.mods.erebus;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;

public class Erebus extends GroovyPropertyContainer {

    public final Composter composter = new Composter();
    public final Smoothie smoothie = new Smoothie();
    public final OfferingAltar offeringAltar = new OfferingAltar();
}
