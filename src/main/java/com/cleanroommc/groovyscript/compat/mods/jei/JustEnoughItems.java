package com.cleanroommc.groovyscript.compat.mods.jei;

import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;

public class JustEnoughItems extends ModPropertyContainer {

    public final Ingredient ingredient = new Ingredient();
    public final Category category = new Category();
    public final Description description = new Description();

    public JustEnoughItems() {
        addRegistry(ingredient);
        addRegistry(category);
        addRegistry(description);
    }

}
