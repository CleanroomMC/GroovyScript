package com.cleanroommc.groovyscript.api.documentation.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows a {@link RegistryDescription} to override any
 * {@link MethodDescription} or {@link RecipeBuilderDescription} declarations.
 * <p>
 * Methods can be referred to by name if there is only one method with the given name
 * in the class, otherwise must be referred to by the method name + method signature.
 * <p>
 * While this can be used for any method, it is preferred to only use it for
 * methods that require it - namely, methods that are declared in a parent class
 * and not overridden in the focused class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({}) // No targets allowed
public @interface MethodOverride {

    /**
     * An override to other {@link MethodDescription} declarations, and only used for {@link MethodDescription} instances which
     * should have custom data for the class they are accessed from.
     *
     * @return array of method description annotations for the class
     * @see MethodDescription
     */
    MethodDescription[] method() default {};

    /**
     * An override to other {@link RecipeBuilderDescription} declarations, and only used for {@link RecipeBuilderDescription} instances which
     * should have custom data for the class they are accessed from.
     *
     * @return array of method description annotations for the class
     * @see RecipeBuilderDescription
     */
    RecipeBuilderDescription[] recipeBuilder() default {};
}
