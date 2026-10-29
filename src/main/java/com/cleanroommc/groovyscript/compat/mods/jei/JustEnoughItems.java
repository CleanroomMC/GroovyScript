/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.compat.mods.jei;

import com.cleanroommc.groovyscript.api.infocommand.InfoParserRegistry;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;

public class JustEnoughItems extends GroovyPropertyContainer {

    public final Ingredient ingredient = new Ingredient();
    public final Category category = new Category();
    public final Description description = new Description();
    public final Catalyst catalyst = new Catalyst();

    @Override
    public void initialize(GroovyContainer<?> owner) {
        InfoParserRegistry.addInfoParser(InfoParserTab.instance);
    }
}
