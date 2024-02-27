package com.cleanroommc.groovyscript.documentation.format;

import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;

import java.util.List;

public class Vue implements IFormat {

    @Override
    public String admonitionStart(Admonition.Format format) {
        switch (format) {
            case COLLAPSED:
                return "::: details";
            case EXPANDED:
                return "::: details {open}";
            default:
                return ":::";
        }
    }

    @Override
    public String admonitionEnd(Admonition.Format format) {
        return ":::\n";
    }

    @Override
    public String codeBlockHighlights(List<String> highlight) {
        if (highlight.isEmpty()) return ":no-line-numbers";
        return ":no-line-numbers {" + String.join(",", highlight) + "}";
    }

}
