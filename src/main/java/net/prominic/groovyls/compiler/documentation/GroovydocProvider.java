package net.prominic.groovyls.compiler.documentation;

import net.prominic.groovyls.compiler.ast.ASTContext;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.jetbrains.annotations.Nullable;

public class GroovydocProvider implements IDocumentationProvider {

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
        for (int i = 1; i < n - 1; i++) {
            line = lines[i];
            int star = line.indexOf("*");
            int at = line.indexOf("@");
            if (at == -1) {
                if (star > -1) // line starts with a *
                {
                    appendLine(markdownBuilder, line.substring(star + 1));
                }
            }
        }
        return markdownBuilder.toString().trim();
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
        // remove all attributes (including namespaced)
        line = line.replaceAll("<(\\w+)(?:\\s+\\w+(?::\\w+)?=(\"|\')[^\"\']*\\2)*\\s*(\\/{0,1})>", "<$1$3>");
        line = line.replaceAll("<pre>", "\n\n```\n");
        line = line.replaceAll("</pre>", "\n```\n");
        line = line.replaceAll("</?(em|i)>", "_");
        line = line.replaceAll("</?(strong|b)>", "**");
        line = line.replaceAll("</?code>", "`");
        line = line.replaceAll("<hr ?\\/>", "\n\n---\n\n");
        line = line.replaceAll("<(p|ul|ol|dl|li|dt|table|tr|div|blockquote)>", "\n\n");

        // to add a line break to markdown, there needs to be at least two
        // spaces at the end of the line
        line = line.replaceAll("<br\\s*/?>\\s*", "  \n");
        line = line.replaceAll("<\\/{0,1}\\w+\\/{0,1}>", "");
        return line;
    }
}
