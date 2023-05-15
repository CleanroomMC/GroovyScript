package com.cleanroommc.groovyscript.compat.mods.botania.recipe;

import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;

public class OrechidRecipe {

    public final int weight;
    public final OreDictIngredient output;

    public OrechidRecipe(OreDictIngredient output, int weight) {
        this.weight = weight;
        this.output = output;
    }
}
