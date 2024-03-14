package com.cleanroommc.groovyscript.server;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.GroovyScriptConfig;
import com.cleanroommc.groovyscript.api.GroovyLog;
import net.prominic.groovyls.GroovyLanguageServer;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.services.LanguageClient;

import java.io.File;
import java.net.ServerSocket;

public class GroovyScriptLanguageServer extends GroovyLanguageServer {

    @SuppressWarnings("InfiniteLoopStatement")
    public static void listen(File root) {
        GroovyLog.get().infoMC("Starting Language server");
        var languageServerContext = new GroovyScriptLanguageServerContext();

        while (true) {
            var server = new GroovyScriptLanguageServer(root, languageServerContext);
            try (var serverSocket = new ServerSocket(GroovyScriptConfig.languageServerPort);
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

    public GroovyScriptLanguageServer(File root, GroovyScriptLanguageServerContext languageServerContext) {
        super(new GroovyScriptCompilationUnitFactory(root, languageServerContext), languageServerContext);
    }
}
