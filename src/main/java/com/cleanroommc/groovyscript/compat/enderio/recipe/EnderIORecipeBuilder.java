package com.cleanroommc.groovyscript.compat.enderio.recipe;

import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import crazypants.enderio.base.recipe.RecipeLevel;

public abstract class EnderIORecipeBuilder<T> extends AbstractRecipeBuilder<T> {

    protected RecipeLevel level = RecipeLevel.IGNORE;
    protected int energy;

    public EnderIORecipeBuilder<T> tierSimple() {
        this.level = RecipeLevel.SIMPLE;
        return this;
    }

    public EnderIORecipeBuilder<T> tierNormal() {
        this.level = RecipeLevel.NORMAL;
        return this;
    }

    public EnderIORecipeBuilder<T> tierEnhanced() {
        this.level = RecipeLevel.ADVANCED;
        return this;
    }

    public EnderIORecipeBuilder<T> tierAny() {
        this.level = RecipeLevel.IGNORE;
        return this;
    }

    public EnderIORecipeBuilder<T> energy(int energy) {
        this.energy = energy;
        return this;
    }
}
