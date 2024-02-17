package com.cleanroommc.groovyscript.documentation;

import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdmonitionBuilder {

    private final List<String> note = new ArrayList<>();
    private Admonition.Type type = Admonition.Type.NOTE;
    private String title = "";
    private boolean hasTitle;
    private int indentation;
    private Admonition.Format format = Admonition.Format.EXPANDED;

    public AdmonitionBuilder note(String note) {
        this.note.add(note);
        return this;
    }

    public AdmonitionBuilder note(String... note) {
        this.note.addAll(Arrays.asList(note));
        return this;
    }

    public AdmonitionBuilder note(List<String> note) {
        this.note.addAll(note);
        return this;
    }

    public AdmonitionBuilder type(Admonition.Type type) {
        this.type = type;
        return this;
    }

    public AdmonitionBuilder title(String title) {
        this.title = title;
        return this;
    }

    public AdmonitionBuilder hasTitle(boolean hasTitle) {
        this.hasTitle = hasTitle;
        return this;
    }

    public AdmonitionBuilder indentation(int indentation) {
        this.indentation = indentation;
        return this;
    }

    public AdmonitionBuilder format(Admonition.Format format) {
        this.format = format;
        return this;
    }

    public String generate() {
        StringBuilder out = new StringBuilder();
        String indent = StringUtils.repeat("    ", indentation);

        out.append(indent).append(format);
        out.append(" ").append(type);
        if (hasTitle) out.append(" \"").append(title).append("\"");
        out.append("\n");

        for (int i = 0; i < note.size(); i++) {
            String line = note.get(i);
            if (!line.trim().isEmpty()) out.append(indent).append("    ").append(line);
            if (i < note.size() - 1) out.append("\n");
        }

        return out.toString();
    }

}
