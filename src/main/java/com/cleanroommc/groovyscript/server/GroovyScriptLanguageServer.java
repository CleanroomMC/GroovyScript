package com.cleanroommc.groovyscript.server;

import java.net.ServerSocket;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageClient;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.GroovyScriptConfig;

import net.prominic.groovyls.GroovyLanguageServer;

public class GroovyScriptLanguageServer extends GroovyLanguageServer {

    @SuppressWarnings("InfiniteLoopStatement")
    public static void listen() {
        var languageServerContext = new GroovyScriptLanguageServerContext();

        while (true) {
            GroovyScript.LOGGER.info("Listening on port: {}", GroovyScriptConfig.port);

            var server = new GroovyScriptLanguageServer(languageServerContext);
            try (var serverSocket = new ServerSocket(GroovyScriptConfig.port);
                 var socket = serverSocket.accept()) {

                GroovyScript.LOGGER.info("Accepted connection from: {}", socket.getInetAddress());

                var launcher = Launcher.createLauncher(server, LanguageClient.class, socket.getInputStream(), socket.getOutputStream());
                server.connect(launcher.getRemoteProxy());

                launcher.startListening().get();
            } catch (Exception e) {
                GroovyScript.LOGGER.error("Connection failed", e);
            }
        }
    }

    public GroovyScriptLanguageServer(GroovyScriptLanguageServerContext languageServerContext) {
        super(new GroovyScriptCompilationUnitFactory(languageServerContext), languageServerContext);
    }
}
