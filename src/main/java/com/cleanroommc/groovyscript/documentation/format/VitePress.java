package com.cleanroommc.groovyscript.documentation.format;

import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VitePress implements IFormat {

    @Override
    public String admonitionStart(Admonition.Format format, Admonition.Type type, int indentation, String title) {
        // Technically limits admonitions on VitePress to only have a maximum of 7 depth.
        // If all 7 depth is used, that design should be rethought anyway.
        String front = StringUtils.repeat(":", 10 - indentation);
        switch (format) {
            case COLLAPSED:
                return Stream.of(front, "details", type.toString().toUpperCase(Locale.ROOT), title).filter(StringUtils::isNotBlank).collect(Collectors.joining(" "));
            case EXPANDED:
                return Stream.of(front, "details", type.toString().toUpperCase(Locale.ROOT), title, "{open}").filter(StringUtils::isNotBlank).collect(Collectors.joining(" "));
            default:
                return Stream.of(front, type.toString().toLowerCase(Locale.ROOT), title).filter(StringUtils::isNotBlank).collect(Collectors.joining(" "));
        }
    }

    @Override
    public String admonitionEnd(Admonition.Format format, int indentation) {
        return "\n" + StringUtils.repeat(":", 10 - indentation);
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
