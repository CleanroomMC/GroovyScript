package net.prominic.groovyls.compiler.ast;

import net.prominic.groovyls.compiler.ILanguageServerContext;

public class ASTContext {

    private final ASTNodeVisitor astVisitor;
    private final ILanguageServerContext languageServerContext;

    public ASTContext(ASTNodeVisitor astVisitor, ILanguageServerContext languageServerContext) {
        this.astVisitor = astVisitor;
        this.languageServerContext = languageServerContext;
    }

    public ASTNodeVisitor getVisitor() {
        return astVisitor;
    }

    public ILanguageServerContext getLanguageServerContext() {
        return languageServerContext;
    }
}
