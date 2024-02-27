package com.cleanroommc.groovyscript.documentation.format;

import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MKDocsMaterial implements IFormat {

    @Override
    public String admonitionStart(Admonition.Format format, Admonition.Type type, String title) {
        switch (format) {
            case COLLAPSED:
                return Stream.of(" ", "???", type.toString(), title).filter(StringUtils::isNotBlank).collect(Collectors.joining(" "));
            case EXPANDED:
                return Stream.of(" ", "???+", type.toString(), title).filter(StringUtils::isNotBlank).collect(Collectors.joining(" "));
            default:
                return Stream.of(" ", "!!!", type.toString(), title).filter(StringUtils::isNotBlank).collect(Collectors.joining(" "));
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

    public boolean allowsIndentation() {
        return true;
    }

}
