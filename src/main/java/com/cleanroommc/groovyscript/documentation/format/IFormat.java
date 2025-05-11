package com.cleanroommc.groovyscript.documentation.format;

import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;

import java.util.List;

/**
 * An interface that adds the required methods for a format to operate properly.
 * This is intended to be a private interface for GroovyScript to generate its documentation, and may be subject to sudden changes.
 * <p>
 * This uses {@link OutputFormat} to register the format types.
 */
public interface IFormat {

    /**
     * @return a link to the `groovy/builder.md` file, which is used to inform the user of the Builder design pattern
     */
    String linkToBuilder();

    /**
     * @param format      what admonition format is being used, which controls if the box is static or expandable, and if it is expanded by default or not
     * @param type        what color and symbol are used, and the fallback title if none is specified
     * @param indentation how much indentation is used
     * @param title       the title of the admonition, if defined. otherwise, defaults to the type
     * @return the string to begin the admonition for the given format
     */
    String admonitionStart(Admonition.Format format, Admonition.Type type, int indentation, String title);

    /**
     * @param format      what format is being used
     * @param indentation how much indentation is used
     * @return the string to end the admonition for the given format
     */
    String admonitionEnd(Admonition.Format format, int indentation);

    /**
     * @param highlight a list of strings indicating what lines should be highlighted
     * @return the string to highlight those lines for the given format
     */
    String codeBlockHighlights(List<String> highlight);

    /**
     * @return the String to hide the Table of Contents (sidebar showing Headers on focused file) from the page
     */
    String removeTableOfContentsText();

    /**
     * @return if the format has a title template, which allows customizing additional values for the tab title without impacting the page title or navigation
     */
    boolean hasTitleTemplate();

    /**
     * @return if admonitions allow additional indentation within the block or if doing so will break the Markdown parser
     */
    boolean allowsIndentation();

    /**
     * @return if the format requires a nav file to lay out all the pages and function properly
     */
    boolean requiresNavFile();

    /**
     * @return if the format uses focus in codeblocks. Focus being true also means that annotations are disabled
     */
    boolean usesFocusInCodeBlocks();
}
