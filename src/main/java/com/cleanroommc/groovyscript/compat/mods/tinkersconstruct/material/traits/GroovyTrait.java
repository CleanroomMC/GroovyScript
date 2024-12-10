package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.traits;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import slimeknights.tconstruct.library.modifiers.ModifierTrait;
import slimeknights.tconstruct.library.traits.ITrait;

public class GroovyTrait extends ModifierTrait implements ITrait {

    public GroovyTrait(String identifier, int color, int maxLevel, int countPerLevel) {
        super(identifier, color, maxLevel, countPerLevel);
    }

    public GroovyTrait(String identifier, int color) {
        this(identifier, color, 0, 0);
    }

    public GroovyTrait addItem(IIngredient ingredient, int amountNeeded, int amountMatched) {
        if (ingredient instanceof OreDictIngredient oreDictIngredient) addItem(oreDictIngredient.getOreDict(), amountNeeded, amountMatched);
        else addItem(ingredient.getMatchingStacks()[0], amountNeeded, amountMatched);
        return this;
    }
}
