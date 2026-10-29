/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.api;

import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;

/**
 * Indicates something that represents one or more oredicts, typically an {@link IIngredient}.
 */
public interface IOreDicts {
    // TODO
    //  There are a large number of places currently in the GroovyScript codebase
    //  that check if something is "instanceof OreDictIngredient".
    //  these should be replaced by checks against IOreDicts,
    //  and surrounding code changed appropriately.

    /**
     * @return a collection of oredict strings
     */
    @UnmodifiableView
    Collection<String> getOreDicts();
}
