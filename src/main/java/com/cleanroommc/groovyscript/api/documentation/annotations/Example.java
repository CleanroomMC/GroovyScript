package com.cleanroommc.groovyscript.api.documentation.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A string which will be formatted to create a functional example of
 * the method/recipe builder for both the wiki and the text script files.<br>
 * When combined, all examples created for any given mod should create a fully functional {@code .groovy} file.
 * <ul>
 *     <li>{@link #value()} a string which is inserted inside parameters calling the attached method.</li>
 *     <li>{@link #imports()} an array of packages that must be imported for the examples file to run properly.
 *     Has no impact on the wiki.</li>
 *     <li>{@link #def()} a string which is the name of the variable this {@link Example} will be assigned to.
 *     Ensure via {@link #priority()} that the declaration happens prior to any use of the variable.</li>
 *     <li>{@link #annotations()} an ordered array of comments that will be converted into annotations for the wiki.
 *     Has no impact on the examples file.</li>
 *     <li>{@link #commented()} if the example is commented in the examples file.
 *     Typically used on {@code removeAll()} methods or other methods which could be disruptive or fail to run.
 *     Has no impact on the wiki.</li>
 *     <li>{@link #priority()} is an integer that influences the sorting of the {@link Example} relative to other {@link Example}s.</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({}) // No targets allowed
public @interface Example {

    /**
     * For basic recipe builders, this will be a string that is a valid one-line series of methods to create a Recipe Builder that will
     * pass the {@link com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder#validate() IRecipeBuilder#validate()} check and be successfully registered.
     * {@code ".do('thing').and('other')"}
     * <p>
     * For complex recipe builders, those that have one or more parameters, in the creation method,
     * the above is true after an initial segment for the creation. {@code "('init').do('thing').and('other')"}
     * <p>
     * For methods, this should be exclusive the parameters of the method. e.g. {@code "item('minecraft:clay')"}
     * <p>
     * Any comments for the wiki should be marked by
     * <code>
     * /*()*{@literal /}
     * </code>
     * and then the actual text of the comment should be in an {@link #annotations()}. Multiple comments can be inserted, and replace the comment as expected.
     *
     * @return a string that will generate valid code when processed
     */
    String value() default "";

    /**
     * As imports must be added to the top of the file, any imports must be split out and explicitly noted for the example(s) to function.
     * Some imports are imported to all files by default via {@link com.cleanroommc.groovyscript.sandbox.GroovyScriptSandbox#getImportCustomizer() GroovyScriptSandbox#getImportCustomizer()}.
     * <br>
     * This should contain an array of full import packages, e.g. {@code {"net.minecraft.item.Item", "net.minecraft.item.ItemStack"}}
     *
     * @return an array of imports that must be added for the example(s) to work
     */
    String[] imports() default {};

    /**
     * Some examples require variables of specific objects. When this is a non-empty string, the output of the example will
     * be assigned a variable of this name, such that the output groovy code is
     * <br>
     * <code>
     * def this = {@link Example}
     * </code>
     *
     * @return the name of the variable that the output of this {@link Example} will be set to
     */
    String def() default "";

    /**
     * An ordered array of annotations used as comments for the wiki.
     * <a href="https://squidfunk.github.io/mkdocs-material/reference/annotations">Annotations for Material for MkDocs</a>
     * are specialized comments which take the form of a button that opens a tooltip. They are formatted as either
     * <code>/*()*{@literal /}</code>, or <code>/*()!*{@literal /}</code>, with the {@code !} controlling if the surrounding comment is rendered, with
     * {@code !} being present causing the surrounding comment characters to be removed.<br>
     * In most cases, <code>/*()!*{@literal /}</code> should be used.<br>
     *
     * @return an array of strings which will replace <code>/*()*{@literal /}</code> or <code>/*()!*{@literal /}</code> to create annotations for the wiki
     */
    String[] annotations() default {};

    /**
     * Some examples may conflict with each other, or it may be otherwise beneficial for the code to the commented by default,
     * yet still remain in the examples file.
     * A common example of this is {@code removeAll()} methods, which may remove entries required for other examples, causing errors when
     * the full file is run.
     *
     * @return if the code in the examples should be commented, defaults to {@code false}
     */
    boolean commented() default false;

    /**
     * Priority of the method, relative to other methods in the same class.
     * Priorities sort entries such that lowest is first, then commented examples are last, then number of examples, then the natural order of the first example.
     *
     * @return the example priority (relative to other examples for the same method or Recipe Builder)
     */
    int priority() default 1000;
}
