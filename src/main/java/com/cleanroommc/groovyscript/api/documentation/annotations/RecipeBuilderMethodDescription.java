package com.cleanroommc.groovyscript.api.documentation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * Marks the given Method as a Recipe Builder method, indicating it returns a Recipe Builder class and is used inside a Recipe Builder.<br>
 * A Recipe Builder is a class which follows the Builder design pattern, where each step of the builder returns the builder class,
 * allowing chaining of methods to quickly and cleanly create complicated objects which may or may not require some values.<br>
 * The field element is autogenerated to be equal to the method name by default.
 * <ul>
 *     <li>{@link #method()} either contains nothing if annotated on a method or contains a
 *     string that targets the desired method in conjunction with {@link RecipeBuilderOverride}.
 *     To target a method, if only a single method has the given name, excluding
 *     bridge, non-public, Object, or methods annotated with {@link com.cleanroommc.groovyscript.api.GroovyBlacklist},
 *     the target may be the method name.
 *     Otherwise, the target must be the name and full descriptor of the method.</li>
 *     <li>{@link #field()} is an array that defaults to {@link Method#getName() Method#getName()} if not overridden, and indicates the target field(s) that the method modifies.</li>
 *     <li>{@link #priority()} is an integer that influences the sorting of the {@link RecipeBuilderMethodDescription} relative to other {@link RecipeBuilderMethodDescription}s.</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RecipeBuilderMethodDescription {

    /**
     * If this {@link RecipeBuilderMethodDescription} annotation is attached to a method, this element is set to the name of the method they are attached to.
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
     * @see RecipeBuilderOverride
     */
    String method() default "";

    /**
     * An array of all fields this method modifies. By default, it checks for a field with the same name as the method.
     *
     * @return an array of the names of the field calling the method modifies. Defaults to the method name
     */
    String[] field() default {};

    /**
     * Priority of the method, relative to other {@link RecipeBuilderMethodDescription}s modifying the shared {@link Property}.
     * Priorities sort entries such that lowest is first, then by the length of {@link Method#getName() Method#getName()}, then by {@link String#compareToIgnoreCase} of {@link Method#getName() Method#getName()}.
     *
     * @return the method priority
     */
    int priority() default 1000;
}
