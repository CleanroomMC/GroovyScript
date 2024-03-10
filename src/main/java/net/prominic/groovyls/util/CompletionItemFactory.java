package net.prominic.groovyls.util;

import net.prominic.groovyls.compiler.ast.ASTContext;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.eclipse.lsp4j.*;

public class CompletionItemFactory {

    public static CompletionItem createCompletion(ASTNode node, String label, ASTContext astContext) {
        var completionItem = createCompletion(GroovyLanguageServerUtils.astNodeToCompletionItemKind(node), label);

        if (node instanceof AnnotatedNode annotatedNode) {
            var documentation = astContext.getLanguageServerContext().getDocumentationFactory().getDocumentation(annotatedNode, astContext);

            if (documentation != null) {
                completionItem.setDocumentation(new MarkupContent(MarkupKind.MARKDOWN, documentation));
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
        } else if (kind == CompletionItemKind.Property || kind == CompletionItemKind.Field) {
            item.setInsertTextFormat(InsertTextFormat.Snippet);
            item.setInsertText(label + ".$0");
        }
        return item;
    }
}
