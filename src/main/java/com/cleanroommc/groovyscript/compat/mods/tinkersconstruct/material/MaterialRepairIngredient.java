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
