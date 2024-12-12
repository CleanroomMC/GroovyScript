package com.cleanroommc.groovyscript.compat.mods.tinkersconstruct.material.armory;

import c4.conarm.lib.modifiers.ArmorModifierTrait;
import c4.conarm.lib.traits.IArmorTrait;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;

public class GroovyArmorTrait extends ArmorModifierTrait implements IArmorTrait {

    public GroovyArmorTrait(String identifier, int color) {
        this(identifier, color, 0, 0);
    }

    public GroovyArmorTrait(String identifier, int color, int maxLevel, int countPerLevel) {
        super(identifier, color, maxLevel, countPerLevel);
    }

    public GroovyArmorTrait addItem(IIngredient ingredient, int amountNeeded, int amountMatched) {
        if (ingredient instanceof OreDictIngredient oreDictIngredient) addItem(oreDictIngredient.getOreDict(), amountNeeded, amountMatched * 144);
        else addItem(ingredient.getMatchingStacks()[0], amountNeeded, amountMatched * 144);
        return this;
    }
}
