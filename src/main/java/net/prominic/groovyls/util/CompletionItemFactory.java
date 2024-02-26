package net.prominic.groovyls.util;

import org.codehaus.groovy.ast.ASTNode;
import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionItemKind;
import org.eclipse.lsp4j.InsertTextFormat;

public class CompletionItemFactory {

    public static CompletionItem createCompletion(ASTNode node, String label) {
        return createCompletion(GroovyLanguageServerUtils.astNodeToCompletionItemKind(node), label);
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
