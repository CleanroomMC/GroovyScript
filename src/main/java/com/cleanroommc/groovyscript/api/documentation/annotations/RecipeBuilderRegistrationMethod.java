package com.cleanroommc.groovyscript.api.documentation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the method is the final step used in a RecipeBuilder, and adds the recipe created
 * by the prior methods of the RecipeBuilder to the registry.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RecipeBuilderRegistrationMethod {

    /**
     * Hierarchy of the property, relative to other properties applying to the same method.
     * Annotations on methods that return {@link Object} are of lower priority by default.
     *
     * @return the property hierarchy (where lower overrides hider)
     */
    int hierarchy() default 10;

    /**
     * Priority of the registration method, relative to other registration methods of the same Recipe Builder.
     * Priorities sort entries such that lowest is first, then by the natural order of {@link com.cleanroommc.groovyscript.registry.VirtualizedRegistry#getName() VirtualizedRegistry#getName()}
     *
     * @return the method priority
     */
    int priority() default 1000;
}
