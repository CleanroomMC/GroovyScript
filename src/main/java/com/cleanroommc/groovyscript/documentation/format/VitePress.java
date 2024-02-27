package com.cleanroommc.groovyscript.documentation.format;

import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;

import java.util.List;
import java.util.Locale;

public class VitePress implements IFormat {

    @Override
    public String admonitionStart(Admonition.Format format, Admonition.Type type, String title) {
        switch (format) {
            case COLLAPSED:
                return String.join(" ", "::: details", type.toString().toUpperCase(Locale.ROOT), title);
            case EXPANDED:
                return String.join(" ", "::: details", type.toString().toUpperCase(Locale.ROOT), title, "{open}");
            default:
                return String.join(" ", ":::", type.toString().toLowerCase(Locale.ROOT), title);
        }
    }

    @Override
    public String admonitionEnd(Admonition.Format format) {
        return "\n:::";
    }

    @Override
    public String codeBlockHighlights(List<String> highlight) {
        if (highlight.isEmpty()) return ":no-line-numbers";
        return ":no-line-numbers {" + String.join(",", highlight) + "}";
    }

    public boolean allowsIndentation() {
        return false;
    }

}
