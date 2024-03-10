package com.cleanroommc.groovyscript.documentation.format;

import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class MKDocsMaterial implements IFormat {

    public String linkToBuilder() {
        return "../../../groovy/builder.md";
    }

    @Override
    public String admonitionStart(Admonition.Format format, Admonition.Type type, int indentation, String title) {
        return (switch (format) {
            case COLLAPSED -> Lists.newArrayList("???", type.toString(), title);
            case EXPANDED -> Lists.newArrayList("???+", type.toString(), title);
            case STANDARD -> Lists.newArrayList("!!!", type.toString(), title);
        }).stream().filter(StringUtils::isNotBlank).collect(Collectors.joining(" "));
    }

    @Override
    public String admonitionEnd(Admonition.Format format, int indentation) {
        return "";
    }

    @Override
    public String codeBlockHighlights(List<String> highlight) {
        if (highlight.isEmpty()) return "";
        return " hl_lines=\"" + String.join(" ", highlight) + "\"";
    }

    public String removeTableOfContentsText() {
        return "hide: toc";
    }

    public boolean hasTitleTemplate() {
        return false;
    }

    public boolean allowsIndentation() {
        return true;
    }

    public boolean requiresNavFile() {
        return true;
    }

    public boolean usesFocusInCodeBlocks() {
        return false;
    }

}
