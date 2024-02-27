package com.cleanroommc.groovyscript.documentation.format;

import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;

import java.util.List;

public class MKDocsMaterial implements IFormat {

    @Override
    public String admonitionStart(Admonition.Format format) {
        switch (format) {
            case COLLAPSED:
                return "???";
            case EXPANDED:
                return "???+";
            default:
                return "!!!";
        }
    }

    @Override
    public String admonitionEnd(Admonition.Format format) {
        return "";
    }

    @Override
    public String codeBlockHighlights(List<String> highlight) {
        if (highlight.isEmpty()) return "";
        return " hl_lines=\"" + String.join(" ", highlight) + "\"";
    }

}
