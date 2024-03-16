package net.prominic.groovyls.compiler.documentation;

import net.prominic.groovyls.compiler.ast.ASTContext;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.jetbrains.annotations.Nullable;

public interface IDocumentationProvider {

    @Nullable String getDocumentation(AnnotatedNode node, ASTContext context);
}
