package com.cleanroommc.groovyscript.api.documentation.annotations;

import com.google.common.base.CaseFormat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Instructions on how to create the desired Admonition for documenting the Registry, based on the syntax to make an
 * <a href="https://squidfunk.github.io/mkdocs-material/reference/admonitions">Admonition for Material for MkDocs</a>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({}) // No targets allowed
public @interface Admonition {

    /**
     * The localization key for the admonition description, should be in the format of
     * <br>
     * <code>
     * groovyscript.wiki.{@link com.cleanroommc.groovyscript.registry.VirtualizedRegistry#getName() {name}}.note{index}
     * </code>
     *
     * @return localization key for the admonition description
     */
    String value();

    /**
     * The format of the Admonition box.
     *
     * @return the type of Admonition box to use, defaults to {@link Format#EXPANDED}
     * @see Format
     */
    Format format() default Format.EXPANDED;

    /**
     * The type of Admonition.
     * The Admonition type controls the symbol, color, and default title.
     * The title may be overridden by enabling {@link #hasTitle()}, after which the value of {@link #title()} will control the title.
     *
     * @return the type of Admonition, controlling symbol, color, and default title, defaults to {@link Type#NOTE}
     * @see Type
     */
    Type type() default Type.NOTE;

    /**
     * If {@link #hasTitle()} is enabled, overrides the title set by the type in {@link #type()}. When it is an empty string
     * the title will be cleared and the title bar will be removed from the Admonition.
     *
     * @return title of the Admonition, with an empty string clears the default title set by the time, defaults to clearing the title
     */
    String title() default "";

    /**
     * Controls if the title set by {@link #title()} is applied and overrides the default title set by the type in {@link #type()}.
     *
     * @return enables the custom {@link #title()}, defaults to {@code false}
     */
    boolean hasTitle() default false;


    /**
     * Informs the rendering and format style of the Admonition.
     */
    enum Format {
        /**
         * A box which is fully expanded at all times.
         */
        STANDARD,
        /**
         * A box which can be toggled between only rendering the title and the full text. Defaults to the collapsed form (only the title visible).
         */
        COLLAPSED,
        /**
         * A box which can be toggled between only rendering the title and the full text. Defaults to the expanded form (both title and text visible).
         */
        EXPANDED
    }


    /**
     * Informs what the symbol, color, and default title of the Admonition are.
     * Commonly used Admonition Types:
     * <br>- {@link #NOTE Note}: Indicates a generic comment
     * <br>- {@link #DANGER Danger}: Indicates something that should be engaged with caution to prevent issues.
     * <br>- {@link #EXAMPLE Example}: Indicates an example of some code in practice.
     */
    enum Type {

        /**
         * Indicates a generic comment - the most common type of Admonition.
         */
        NOTE,
        /**
         * Indicates a summary of detailed and complex information.
         */
        ABSTRACT,
        /**
         * Indicates that this is just generic information.
         */
        INFO,
        /**
         * Indicates a suggestion on how to use something in a better or easier way.
         */
        TIP,
        /**
         * Indicates something that should be done, typically in relation to {@link #FAILURE Failure}.
         */
        SUCCESS,
        /**
         * Indicates a question or answer to a Frequently Asked Question.
         */
        QUESTION,
        /**
         * Indicates something that should be engaged with caution to prevent unexpected outcomes.
         */
        WARNING,
        /**
         * Indicates a possible failure result or an example of code that fails.
         */
        FAILURE,
        /**
         * Indicates something that should be engaged with caution to prevent issues.
         */
        DANGER,
        /**
         * Indicates that something is broken.
         */
        BUG,
        /**
         * Indicates an example of some code in practice.
         */
        EXAMPLE,
        /**
         * Indicates a quote of an external source.
         */
        QUOTE;

        public String toString() {
            return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
        }
    }
}
