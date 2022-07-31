package com.cleanroommc.groovyscript.compat.enderio;

import crazypants.enderio.base.recipe.IRecipe;

import java.util.List;

public interface IEnderIORecipes {

    List<IRecipe> findRecipes(Object... data);

    void removeRecipes(Object... data);
}
