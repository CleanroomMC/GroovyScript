package com.cleanroommc.groovyscript.documentation;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

public class CodeBlockBuilder {


    private final List<String> lines = new ArrayList<>();
    private final List<String> annotations = new ArrayList<>();
    private final List<String> highlight = new ArrayList<>();
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

    public CodeBlockBuilder indentation(int indentation) {
        this.indentation = indentation;
        return this;
    }

    public List<String> generate() {
        List<String> out = new ArrayList<>();
        String indent = StringUtils.repeat("    ", indentation);

        String hl_lines = highlight.isEmpty() ? "" : (" hl_lines=\"" + String.join(" ", highlight) + "\"");

        out.add(indent + "```" + lang + hl_lines);
        for (String line : lines) out.add(indent + line);
        out.add(indent + "```");

        if (!annotations.isEmpty()) out.add("");

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

        out.add("\n");

        return out;
    }

    public String toString() {
        return String.join("\n", generate());
    }

}
