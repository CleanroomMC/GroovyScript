package net.prominic.groovyls.util;

import net.prominic.groovyls.compiler.ast.ASTContext;
import net.prominic.groovyls.compiler.documentation.DocumentationFactory;
import org.codehaus.groovy.ast.*;
import org.eclipse.lsp4j.*;

public class CompletionItemFactory {

    public static CompletionItem createCompletion(ASTNode node, String label, ASTContext astContext) {
        var completionItem = createCompletion(node, label);
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

    public static CompletionItem createCompletion(ASTNode node, String label) {
        if (node instanceof MethodNode mn) {
            var item = createCompletion(CompletionItemKind.Method, label);
            int params = mn.getParameters() == null ? 0 : mn.getParameters().length;
            item.setInsertTextFormat(InsertTextFormat.Snippet);
            if (params > 0) {
                Parameter p = mn.getParameters()[params - 1];
                ClassNode type = p.getType();
                if (!p.isDynamicTyped() && type.getComponentType() != null) {
                    params--; // last arg is array or varargs -> don't count it in
                }
            }
            if (params == 0) {
                item.setInsertText(label + "()");
                return item;
            }
            StringBuilder builder = new StringBuilder(label).append('(');
            for (int i = 0; i < params; i++) {
                builder.append('$').append(i + 1);
                if (i < params - 1) builder.append(", ");
            }
            item.setInsertText(builder.append(")$0").toString());
            return item;
        }
        return createCompletion(GroovyLSUtils.astNodeToCompletionItemKind(node), label);
    }

    public static CompletionItem createCompletion(CompletionItemKind kind, String label) {
        var item = new CompletionItem();
        item.setKind(kind);
        item.setLabel(label);
        return item;
    }

    public static CompletionItem createKeywordCompletion(String keyword, boolean popular) {
        return createKeywordCompletion(keyword, popular, null);
    }

    public static CompletionItem createKeywordCompletion(String keyword, boolean popular, String insert) {
        boolean insertSpace = keyword.endsWith(" ");
        String realKeyword = insertSpace ? keyword.substring(0, keyword.length() - 1) : keyword;
        var item = new CompletionItem(realKeyword);
        item.setKind(CompletionItemKind.Keyword);
        String sort = popular ? "zz" : "zzz";
        item.setSortText(sort + realKeyword);
        item.setInsertTextFormat(InsertTextFormat.Snippet);
        if (insert != null) {
            item.setInsertText(realKeyword + insert);
        } else if (insertSpace) {
            item.setInsertText(realKeyword + " ");
        }
        return item;
    }
}
