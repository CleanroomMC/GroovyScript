package com.cleanroommc.groovyscript.documentation.annotations;

import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;

import java.lang.annotation.*;

/**
 * Indicates that the method is the final step used in a RecipeBuilder, and adds the recipe created
 * by the prior methods of the RecipeBuilder to the registry.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RecipeBuilderRegistrationMethod {

    /**
     * Priority of the registration method, relative to other registration methods of the same Recipe Builder.
     * Priorities sort entries such that lowest is first, then by the natural order of {@link VirtualizedRegistry#getName()}
     *
     * @return the method priority
     */
    int priority() default 1000;
}
