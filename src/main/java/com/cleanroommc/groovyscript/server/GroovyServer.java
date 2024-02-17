package com.cleanroommc.groovyscript.server;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.GroovyScriptConfig;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.*;

import java.net.ServerSocket;
import java.util.concurrent.CompletableFuture;

public class GroovyServer implements LanguageServer, LanguageClientAware {

    @SuppressWarnings("InfiniteLoopStatement")
    public static void listen() {
        while (true) {
            GroovyScript.LOGGER.info("Listening on port: {}", GroovyScriptConfig.port);

            var server = new GroovyServer();
            try (var serverSocket = new ServerSocket(GroovyScriptConfig.port);
                 var socket = serverSocket.accept()) {
                var launcher = LSPLauncher.createServerLauncher(server, socket.getInputStream(), socket.getOutputStream());
                server.connect(launcher.getRemoteProxy());
                launcher.startListening().get();
            } catch (Exception e) {
                GroovyScript.LOGGER.error("Connection failed", e);
            }
        }
    }

    public ClientCapabilities clientCapabilities;
    public LanguageClient client;

    @Override
    public void connect(LanguageClient client) {
        this.client = client;
        GroovyScript.LOGGER.info("Connected!");
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        clientCapabilities = params.getCapabilities();

        var serverCapabilities = new ServerCapabilities();
        var serverInfo = new ServerInfo("GroovyScript");

        return CompletableFuture.completedFuture(new InitializeResult(serverCapabilities, serverInfo));
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void exit() {}

    @Override
    public void cancelProgress(WorkDoneProgressCancelParams params) {}

    @Override
    public void setTrace(SetTraceParams params) {}

    @Override
    public TextDocumentService getTextDocumentService() {
        return null;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return null;
    }
}
