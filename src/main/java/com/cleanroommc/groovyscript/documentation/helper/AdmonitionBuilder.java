package com.cleanroommc.groovyscript.documentation.helper;

import com.cleanroommc.groovyscript.api.documentation.annotations.Admonition;
import com.cleanroommc.groovyscript.documentation.Documentation;
import com.cleanroommc.groovyscript.documentation.format.IFormat;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdmonitionBuilder {

    private final List<String> note = new ArrayList<>();
    private Admonition.Type admonitionType = Admonition.Type.NOTE;
    private String title = "";
    private boolean hasTitle;
    private int indentation;
    private Admonition.Format admonitionFormat = Admonition.Format.EXPANDED;

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
        this.admonitionType = type;
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
        this.admonitionFormat = format;
        return this;
    }

    public String generate() {
        return generate(Documentation.DEFAULT_FORMAT);
    }

    public String generate(IFormat format) {
        StringBuilder out = new StringBuilder();
        String indent = StringUtils.repeat("    ", indentation);

        out.append(format.admonitionStart(admonitionFormat, admonitionType, indentation, hasTitle ? title : ""));
        out.append("\n");

        for (int i = 0; i < note.size(); i++) {
            String line = note.get(i);
            if (!line.trim().isEmpty()) {
                if (format.allowsIndentation()) out.append(indent).append("    ");
                out.append(line);
            }
            if (i < note.size() - 1) out.append("\n");
        }

        out.append(indent).append(format.admonitionEnd(admonitionFormat, indentation));

        return out.toString();
    }
}
