package net.prominic.groovyls.compiler.documentation;

import net.prominic.groovyls.compiler.ast.ASTContext;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.jetbrains.annotations.Nullable;

public class GroovydocProvider implements IDocumentationProvider {

    public static final String PARAM_TAG = "@param";
    public static final String RETURN_TAG = "@return";
    public static final String THROWS_TAG = "@throws";

    @Override
    public @Nullable String getDocumentation(AnnotatedNode node, ASTContext context) {
        var groovydoc = node.getGroovydoc();

        if (groovydoc == null || !groovydoc.isPresent()) {
            return null;
        }
        String content = groovydoc.getContent();
        String[] lines = content.split("\n");
        StringBuilder markdownBuilder = new StringBuilder();
        int n = lines.length;
        if (n == 1) {
            // strip end of groovydoc comment
            int c = lines[0].indexOf("*/");
            if (c != -1) {
                lines[0] = lines[0].substring(0, c);
            }
        }
        // strip start of groovydoc coment
        String line = lines[0];
        int lengthToRemove = Math.min(line.length(), 3);
        line = line.substring(lengthToRemove);
        appendLine(markdownBuilder, line);
        boolean paramMode = false;
        boolean throwMode = false;
        for (int i = 1; i < n - 1; i++) {
            line = lines[i];
            int star = line.indexOf("*");
            int at = line.indexOf("@");
            if (at >= 0) {
                if (at == line.indexOf(PARAM_TAG, at)) {
                    if (!paramMode) {
                        paramMode = true;
                        throwMode = false;
                        appendLine(markdownBuilder, "Params:");
                    }
                    appendLine(markdownBuilder, " - " + line.substring(at + PARAM_TAG.length()).trim());
                } else if (at == line.indexOf(RETURN_TAG, at)) {
                    appendLine(markdownBuilder, "Returns " + line.substring(at + RETURN_TAG.length()).trim());
                } else if (at == line.indexOf(THROWS_TAG)) {
                    if (!throwMode) {
                        paramMode = false;
                        throwMode = true;
                        appendLine(markdownBuilder, "Throws:");
                    }
                    appendLine(markdownBuilder, " - " + line.substring(at + THROWS_TAG.length()).trim());
                }
            } else if (star >= 0) {
                // line starts with a *
                appendLine(markdownBuilder, line.substring(star + 1));
            }

        }
        return markdownBuilder.toString().

                trim();
    }

    @Override
    public @Nullable String getSortText(AnnotatedNode node, ASTContext context) {
        return null;
    }

    private static void appendLine(StringBuilder markdownBuilder, String line) {
        line = reformatLine(line);
        if (line.length() == 0) {
            return;
        }
        markdownBuilder.append(line);
        markdownBuilder.append("\n");
    }

    private static String reformatLine(String line) {
        // remove all attributes (including namespace)
        line = line.replaceAll("<(\\w+)(?:\\s+\\w+(?::\\w+)?=([\"'])[^\"']*\\2)*\\s*(/?)>", "<$1$3>");
        line = line.replaceAll("<pre>", "\n\n```\n");
        line = line.replaceAll("</pre>", "\n```\n");
        line = line.replaceAll("</?(em|i)>", "_");
        line = line.replaceAll("</?(strong|b)>", "**");
        line = line.replaceAll("</?code>", "`");
        line = line.replaceAll("<hr ?/>", "\n\n---\n\n");
        line = line.replaceAll("<(p|ul|ol|dl|li|dt|table|tr|div|blockquote)>", "\n\n");

        // to add a line break to markdown, there needs to be at least two
        // spaces at the end of the line
        line = line.replaceAll("<br\\s*/?>\\s*", "  \n");
        line = line.replaceAll("</?\\w+/?>", "");
        return line;
    }

}
