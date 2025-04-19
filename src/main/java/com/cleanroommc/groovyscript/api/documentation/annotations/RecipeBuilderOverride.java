package com.cleanroommc.groovyscript.api.documentation.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows a {@link RegistryDescription} to override any
 * {@link RecipeBuilderMethodDescription}, {@link RecipeBuilderRegistrationMethod}, or {@link Property} declarations.
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
public @interface RecipeBuilderOverride {

    /**
     * An override to other {@link Property} declarations,
     * and should only be used for {@link Property} instances which
     * should have custom data for the class they are accessed from.
     *
     * @return array of property annotations for fields for the builder class
     */
    Property[] requirement() default {};

    /**
     * An override to other {@link RecipeBuilderMethodDescription} declarations,
     * and should only be used for {@link RecipeBuilderMethodDescription} instances which
     * should have custom data for the class they are accessed from.
     *
     * @return array of method description annotations for the builder class
     */
    RecipeBuilderMethodDescription[] method() default {};

    /**
     * An override to other {@link RecipeBuilderRegistrationMethod} declarations,
     * and should only be used for {@link RecipeBuilderRegistrationMethod} instances which
     * should have custom data for the class they are accessed from.
     *
     * @return array of registry annotations for the builder class
     */
    RecipeBuilderRegistrationMethod[] register() default {};
}
