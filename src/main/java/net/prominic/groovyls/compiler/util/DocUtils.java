package net.prominic.groovyls.compiler.util;

import net.prominic.groovyls.compiler.ast.ASTContext;
import org.codehaus.groovy.ast.AnnotatedNode;

public class DocUtils {

    public static String getMarkdownDescription(AnnotatedNode node, ASTContext context) {
        var doc = GroovydocUtils.groovydocToMarkdownDescription(node.getGroovydoc());
        if (doc == null) {
            doc = MkDocUtils.mkDocToMarkdownDescription(node, context);
        }
        return doc;
    }
}
