package net.prominic.groovyls.compiler.documentation;

import net.prominic.groovyls.compiler.ast.ASTContext;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.jetbrains.annotations.Nullable;

public class DocumentationFactory {

    private final IDocumentationProvider[] providers;

    public DocumentationFactory(IDocumentationProvider... providers) {
        this.providers = providers;
    }

    public @Nullable String getDocumentation(AnnotatedNode node, ASTContext context) {
        for (IDocumentationProvider provider : providers) {
            String documentation = provider.getDocumentation(node, context);
            if (documentation != null) {
                return documentation;
            }
        }
        return null;
    }
}
