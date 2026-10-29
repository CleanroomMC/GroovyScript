/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.compat.mods.bloodmagic;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;

public class BloodMagic extends GroovyPropertyContainer {

    public final BloodAltar bloodAltar = new BloodAltar();
    public final AlchemyArray alchemyArray = new AlchemyArray();
    public final TartaricForge tartaricForge = new TartaricForge();
    public final AlchemyTable alchemyTable = new AlchemyTable();
    public final Tranquility tranquility = new Tranquility();
    public final Sacrificial sacrificial = new Sacrificial();
    public final Meteor meteor = new Meteor();
}
