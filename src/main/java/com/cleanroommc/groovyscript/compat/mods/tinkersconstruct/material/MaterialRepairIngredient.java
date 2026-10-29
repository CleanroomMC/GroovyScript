/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material;

import com.cleanroommc.groovyscript.api.IIngredient;

@SuppressWarnings("ClassCanBeRecord")
public class MaterialRepairIngredient {

    public final IIngredient ingredient;
    public final int amountNeeded;
    public final int amountMatched;

    public MaterialRepairIngredient(IIngredient ingredient, int amountNeeded, int amountMatched) {
        this.ingredient = ingredient;
        this.amountNeeded = amountNeeded;
        this.amountMatched = amountMatched;
    }
}
