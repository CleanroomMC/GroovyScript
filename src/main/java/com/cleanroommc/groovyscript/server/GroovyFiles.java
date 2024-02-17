package com.cleanroommc.groovyscript.server;

import org.eclipse.lsp4j.*;

public class GroovyFiles {
    private final GroovyServer server;
    
    protected GroovyFiles(GroovyServer server) {
        this.server = server;
    }

    public void didOpen(DidOpenTextDocumentParams params) {}

    public void didChange(DidChangeTextDocumentParams params) {}

    public void didClose(DidCloseTextDocumentParams params) {}

    public void didSave(DidSaveTextDocumentParams params) {}

    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {}
}
