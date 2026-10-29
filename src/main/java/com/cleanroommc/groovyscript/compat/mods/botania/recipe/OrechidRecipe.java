/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.compat.mods.botania.recipe;

@SuppressWarnings("ClassCanBeRecord")
public class OrechidRecipe {

    public final int weight;
    public final String output;

    public OrechidRecipe(String output, int weight) {
        this.weight = weight;
        this.output = output;
    }
}
