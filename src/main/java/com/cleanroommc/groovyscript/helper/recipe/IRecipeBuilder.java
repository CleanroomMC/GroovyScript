package com.cleanroommc.groovyscript.helper.recipe;

import org.jetbrains.annotations.Nullable;

public interface IRecipeBuilder<T> {

    boolean validate();

    @Nullable
    T register();
}
