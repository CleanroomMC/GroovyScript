package com.cleanroommc.groovyscript.documentation.format;

import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;

import java.util.List;

public interface IFormat {

    /**
     * @param format what format is being used
     * @return the string to begin the admonition for the given format
     */
    String admonitionStart(Admonition.Format format);

    /**
     * @param format what format is being used
     * @return the string to end the admonition for the given format
     */
    String admonitionEnd(Admonition.Format format);

    /**
     * @param highlight a list of strings indicating what lines should be highlighted
     * @return the string to highlight those lines for the given format
     */
    String codeBlockHighlights(List<String> highlight);

}
