package net.prominic.groovyls.providers;

import net.prominic.groovyls.compiler.ast.ASTContext;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ModuleNode;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class DocProvider {

    protected final URI doc;
    protected final ASTContext astContext;

    protected DocProvider(URI doc, ASTContext context) {
        this.doc = doc;
        this.astContext = context;
    }

    public URI getDocUri() {
        return doc;
    }

    public ModuleNode getModule() {
        return astContext.getVisitor().getModule(doc);
    }

    public List<ASTNode> getNodes() {
        return astContext.getVisitor().getNodes(doc);
    }

    public <T> CompletableFuture<T> future(T t) {
        return CompletableFuture.completedFuture(t);
    }
}
