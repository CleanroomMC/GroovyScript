package net.prominic.groovyls.util;

import net.prominic.groovyls.compiler.ast.ASTContext;
import net.prominic.groovyls.compiler.documentation.DocumentationFactory;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.eclipse.lsp4j.*;

public class CompletionItemFactory {

    public static CompletionItem createCompletion(ASTNode node, String label, ASTContext astContext) {
        var completionItem = createCompletion(GroovyLSUtils.astNodeToCompletionItemKind(node), label);

        if (node instanceof AnnotatedNode annotatedNode) {
            DocumentationFactory docs = astContext.getLanguageServerContext().getDocumentationFactory();
            var documentation = docs.getDocumentation(annotatedNode, astContext);
            if (documentation != null) {
                completionItem.setDocumentation(new MarkupContent(MarkupKind.MARKDOWN, documentation));
            }
            var sortText = docs.getSortText(annotatedNode, astContext);
            if (sortText != null) {
                completionItem.setSortText(sortText);
            }
        }

        return completionItem;
    }

    public static CompletionItem createCompletion(CompletionItemKind kind, String label) {
        var item = new CompletionItem();
        item.setKind(kind);
        item.setLabel(label);
        if (kind == CompletionItemKind.Method) {
            item.setInsertTextFormat(InsertTextFormat.Snippet);
            item.setInsertText(label + "($0)");
        }
        return item;
    }
}
