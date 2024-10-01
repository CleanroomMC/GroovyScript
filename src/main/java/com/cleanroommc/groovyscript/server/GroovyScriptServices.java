package com.cleanroommc.groovyscript.server;

import com.cleanroommc.groovyscript.sandbox.FileUtil;
import com.cleanroommc.groovyscript.server.features.textureDecoration.TextureDecorationInformation;
import com.cleanroommc.groovyscript.server.features.textureDecoration.TextureDecorationParams;
import com.cleanroommc.groovyscript.server.features.textureDecoration.TextureDecorationProvider;
import net.prominic.groovyls.GroovyServices;
import net.prominic.groovyls.compiler.ILanguageServerContext;
import net.prominic.groovyls.compiler.ast.ASTContext;
import net.prominic.groovyls.config.ICompilationUnitFactory;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GroovyScriptServices extends GroovyServices implements GroovyScriptFeaturesService {

    public GroovyScriptServices(ICompilationUnitFactory factory, ILanguageServerContext languageServerContext) {
        super(factory, languageServerContext);
    }

    @Override
    public CompletableFuture<List<TextureDecorationInformation>> textureDecoration(TextureDecorationParams params) {
        URI uri = FileUtil.fixUri(params.getTextDocument().getUri());
        var unit = compilationUnitFactory.create(workspaceRoot, uri);

        var visitor = compileAndVisitAST(unit, uri);

        if (visitor == null) {
            return CompletableFuture.completedFuture(null);
        }

        var provider = new TextureDecorationProvider(uri, new ASTContext(visitor, languageServerContext));
        return provider.provideTextureDecorations();
    }
}
