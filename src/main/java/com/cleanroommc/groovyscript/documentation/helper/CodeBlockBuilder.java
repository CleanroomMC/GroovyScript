package com.cleanroommc.groovyscript.documentation.helper;

import com.cleanroommc.groovyscript.documentation.Documentation;
import com.cleanroommc.groovyscript.documentation.format.IFormat;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

public class CodeBlockBuilder {

    private final List<String> lines = new ArrayList<>();
    private final List<String> annotations = new ArrayList<>();
    private final List<String> highlight = new ArrayList<>();
    private final List<Integer> focus = new ArrayList<>();
    private String lang = "groovy";
    private int indentation;

    public CodeBlockBuilder line(String line) {
        this.lines.add(line);
        return this;
    }

    public CodeBlockBuilder line(String... lines) {
        this.lines.addAll(Arrays.asList(lines));
        return this;
    }

    public CodeBlockBuilder line(List<String> lines) {
        this.lines.addAll(lines);
        return this;
    }

    public CodeBlockBuilder annotation(String annotation) {
        this.annotations.add(annotation);
        return this;
    }

    public CodeBlockBuilder annotation(String... annotations) {
        this.annotations.addAll(Arrays.asList(annotations));
        return this;
    }

    public CodeBlockBuilder annotation(List<String> annotations) {
        this.annotations.addAll(annotations);
        return this;
    }

    public CodeBlockBuilder lang(String lang) {
        this.lang = lang;
        return this;
    }

    public CodeBlockBuilder highlight(String highlight) {
        this.highlight.add(highlight);
        return this;
    }

    public CodeBlockBuilder focus(Integer focus) {
        this.focus.add(focus);
        return this;
    }

    public CodeBlockBuilder indentation(int indentation) {
        this.indentation = indentation;
        return this;
    }

    public List<String> generate() {
        return generate(Documentation.DEFAULT_FORMAT);
    }

    @SuppressWarnings("StringBufferMayBeStringBuilder")
    public List<String> generate(IFormat format) {
        List<String> out = new ArrayList<>();
        String indent = StringUtils.repeat("    ", indentation);

        out.add(indent + "```" + lang + format.codeBlockHighlights(highlight));
        for (String line : lines) out.add(indent + line);
        out.add(indent + "```");

        if (!annotations.isEmpty()) out.add("");

        if (format.usesFocusInCodeBlocks()) {
            int i = 0;
            for (int x = 0; x < out.size(); x++) {
                Matcher matcher = Documentation.ANNOTATION_COMMENT_LOCATION.matcher(out.get(x));
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    matcher.appendReplacement(sb, String.format("/* %s */", annotations.get(i)));
                    i++;
                }
                matcher.appendTail(sb);
                if (focus.contains(x)) out.set(x, sb + " // [!code focus]");
                else out.set(x, sb.toString());
            }
        } else {
            int i = 0;
            for (int x = 0; x < out.size(); x++) {
                Matcher matcher = Documentation.ANNOTATION_COMMENT_LOCATION.matcher(out.get(x));
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    matcher.appendReplacement(sb, String.format("/*(%d)$1*/", 1 + i));
                    out.add(String.format("%s%d. %s", indent, 1 + i, annotations.get(i)));
                    i++;
                }
                matcher.appendTail(sb);
                out.set(x, sb.toString());
            }
        }


        out.add("\n");

        return out;
    }

    public String toString() {
        return String.join("\n", generate());
    }
}
