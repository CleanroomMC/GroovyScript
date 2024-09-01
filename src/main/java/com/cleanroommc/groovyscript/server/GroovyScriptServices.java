package com.cleanroommc.groovyscript.server;

import com.cleanroommc.groovyscript.sandbox.FileUtil;
import com.cleanroommc.groovyscript.server.features.textureDecoration.TextureDecorationInformation;
import com.cleanroommc.groovyscript.server.features.textureDecoration.TextureDecorationParams;
import com.cleanroommc.groovyscript.server.features.textureDecoration.TextureDecorationProvider;
import net.prominic.groovyls.GroovyServices;
import net.prominic.groovyls.compiler.ILanguageServerContext;
import net.prominic.groovyls.compiler.ast.ASTContext;
import net.prominic.groovyls.config.ICompilationUnitFactory;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GroovyScriptServices extends GroovyServices {

    public GroovyScriptServices(ICompilationUnitFactory factory, ILanguageServerContext languageServerContext) {
        super(factory, languageServerContext);
    }

    @JsonRequest(value = "groovyScript/textureDecoration", useSegment = false)
    public CompletableFuture<List<TextureDecorationInformation>> textureDecoration(TextureDecorationParams params) {
        URI uri = FileUtil.fixUri(params.getTextDocument().getUri());
        var unit = compilationUnitFactory.create(workspaceRoot, uri);

        var visitor = compileAndVisitAST(unit, uri);

        if (visitor == null) {
            return CompletableFuture.completedFuture(null);
        }

        var provider = new TextureDecorationProvider(new ASTContext(visitor, languageServerContext));
        return provider.provideTextureDecorations(params.getTextDocument());
    }
}
