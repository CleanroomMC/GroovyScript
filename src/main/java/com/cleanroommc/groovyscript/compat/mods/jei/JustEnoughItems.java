package com.cleanroommc.groovyscript.compat.mods.jei;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class JustEnoughItems extends ModPropertyContainer {

    public final Ingredient ingredient = new Ingredient();

    public JustEnoughItems() {
        addRegistry(ingredient);
    }

}
