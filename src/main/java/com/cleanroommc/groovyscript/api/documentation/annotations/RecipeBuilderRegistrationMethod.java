package com.cleanroommc.groovyscript.api.documentation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the method is the final step used in a RecipeBuilder, and adds the recipe created
 * by the prior methods of the RecipeBuilder to the registry.
 * <br>
 * In most cases, there will only be a single registration method.
 * By convention, the name for this method should be {@code register}.
 *
 * <ul>
 *     <li>{@link #method()} either contains nothing if annotated on a method or contains a
 *     string that targets the desired method in conjunction with {@link RecipeBuilderOverride}.
 *     To target a method, if only a single method has the given name, excluding
 *     bridge, non-public, Object, or methods annotated with {@link com.cleanroommc.groovyscript.api.GroovyBlacklist},
 *     the target may be the method name.
 *     Otherwise, the target must be the name and full descriptor of the method.</li>
 *     <li>{@link #hierarchy()} is an integer that controls the precedence of the {@link RecipeBuilderRegistrationMethod} annotation when multiple versions of it exist for a single field.
 *     A lower hierarchy overrides a higher one, with the default having a value of 10.</li>
 *     <li>{@link #priority()} is an integer that influences the sorting of the {@link RecipeBuilderRegistrationMethod} relative to other {@link RecipeBuilderRegistrationMethod RecipeBuilderRegistrationMethods}.</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RecipeBuilderRegistrationMethod {

    /**
     * If this {@link RecipeBuilderDescription} annotation is attached to a method, this element is set to the name of the method they are attached to.
     * When annotated on a method directly, this should not be set, as it has no functionality.
     * <br>
     * If this is not annotated to a method, this should either be the method name
     * (if only a single method has the given name)
     * or needs to be the name and full descriptor of the method.
     * <br>
     * Methods that are bridge, non-public, Object, or methods annotated with {@link com.cleanroommc.groovyscript.api.GroovyBlacklist}
     * cannot be targeted.
     *
     * @return the target method, if not annotated to a method directly.
     * @see MethodOverride
     */
    String method() default "";

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
