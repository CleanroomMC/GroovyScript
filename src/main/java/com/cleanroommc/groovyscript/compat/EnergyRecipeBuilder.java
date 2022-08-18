package com.cleanroommc.groovyscript.compat;

import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;

public abstract class EnergyRecipeBuilder<T> extends AbstractRecipeBuilder<T> {

    protected int energy;

    public EnergyRecipeBuilder<T> energy(int energy) {
        this.energy = energy;
        return this;
    }
}
