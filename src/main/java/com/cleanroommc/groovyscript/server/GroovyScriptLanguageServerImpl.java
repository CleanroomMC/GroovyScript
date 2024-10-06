package com.cleanroommc.groovyscript.server;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.GroovyScriptConfig;
import com.cleanroommc.groovyscript.api.GroovyLog;
import net.prominic.groovyls.GroovyLanguageServer;
import net.prominic.groovyls.compiler.ILanguageServerContext;
import net.prominic.groovyls.config.ICompilationUnitFactory;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageClient;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.ServerSocket;
import java.util.concurrent.CompletableFuture;

public class GroovyScriptLanguageServerImpl extends GroovyLanguageServer<GroovyScriptServices> implements GroovyScriptLanguageServer {

    @SuppressWarnings("InfiniteLoopStatement")
    public static void listen(File root) {
        GroovyScript.doForGroovyScript(() -> GroovyLog.get().infoMC("Starting Language server"));
        var languageServerContext = new GroovyScriptLanguageServerContext();

        while (true) {
            var server = new GroovyScriptLanguageServerImpl(root, languageServerContext);
            try (var serverSocket = new ServerSocket(GroovyScriptConfig.languageServerPort); var socket = serverSocket.accept()) {

                GroovyScript.doForGroovyScript(() -> GroovyScript.LOGGER.info("Accepted connection from: {}", socket.getInetAddress()));

                var launcher = Launcher.createLauncher(server, LanguageClient.class, socket.getInputStream(), socket.getOutputStream());
                server.connect(launcher.getRemoteProxy());

                launcher.startListening().get();
            } catch (Exception e) {
                GroovyScript.doForGroovyScript(() -> GroovyScript.LOGGER.error("Connection failed", e));
            }
        }
    }

    public GroovyScriptLanguageServerImpl(File root, GroovyScriptLanguageServerContext languageServerContext) {
        super(new GroovyScriptCompilationUnitFactory(root, languageServerContext), languageServerContext);
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        return super.initialize(params).thenApply(initializeResult -> {
            var groovyScriptCapabilities = new GroovyScriptCapabilities();
            groovyScriptCapabilities.setTextureDecorationProvider(true);

            initializeResult.getCapabilities().setExperimental(groovyScriptCapabilities);
            return initializeResult;
        });
    }

    @Override
    public GroovyScriptFeaturesService getGroovyScriptFeaturesService() {
        return groovyServices;
    }

    @Override
    protected @NotNull GroovyScriptServices createGroovyServices(ICompilationUnitFactory compilationUnitFactory,
                                                                 ILanguageServerContext languageServerContext) {
        return new GroovyScriptServices(compilationUnitFactory, languageServerContext);
    }

}
