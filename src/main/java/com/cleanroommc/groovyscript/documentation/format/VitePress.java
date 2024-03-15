package com.cleanroommc.groovyscript.documentation.format;

import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class VitePress implements IFormat {

    public String linkToBuilder() {
        return "../../groovy/builder.md";
    }

    @Override
    public String admonitionStart(Admonition.Format format, Admonition.Type type, int indentation, String title) {
        // Technically limits admonitions on VitePress to only have a maximum of 7 depth.
        // If all 7 depth is used, that design should be rethought anyway.
        String front = StringUtils.repeat(":", 10 - indentation);
        String name = type.toString().toLowerCase(Locale.ROOT);
        String visibleTitle = title.isEmpty() ? type.toString() : title;
        return (switch (format) {
            case COLLAPSED -> Lists.newArrayList(front, "details", visibleTitle, String.format("{id=\"%s\"}", name));
            case EXPANDED -> Lists.newArrayList(front, "details", visibleTitle, String.format("{open id=\"%s\"}", name));
            case STANDARD -> Lists.newArrayList(front, "info", visibleTitle, String.format("{id=\"%s\"}", name));
        }).stream().filter(StringUtils::isNotBlank).collect(Collectors.joining(" "));
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

    public String removeTableOfContentsText() {
        return "aside: false";
    }

    public boolean hasTitleTemplate() {
        return true;
    }

    public boolean allowsIndentation() {
        return false;
    }

    public boolean requiresNavFile() {
        return false;
    }

    public boolean usesFocusInCodeBlocks() {
        return true;
    }

}
