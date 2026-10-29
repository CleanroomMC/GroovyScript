/*
 * Copyright (c) 2022-2026 CleanroomMC contributors
 * SPDX-License-Identifier: LGPL-3.0-only
 */

package com.cleanroommc.groovyscript.helper.recipe;

import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderRegistrationMethod;
import org.jetbrains.annotations.Nullable;

/**
 * A groovy recipe builder template
 *
 * @param <T>
 */
public interface IRecipeBuilder<T> {

    /**
     * Validates the current values. Should be called in {@link #register()}.
     *
     * @return if a valid recipe can be build with the current values
     */
    boolean validate();

    /**
     * This method creates and registers a recipe using the values of this builder.
     * Should call {@link #validate()} and return null if values are not valid.
     *
     * @return the built recipe or null if values are invalid
     */
    @Nullable
    @RecipeBuilderRegistrationMethod
    T register();
}
