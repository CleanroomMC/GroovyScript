package com.cleanroommc.groovyscript.server;

import org.eclipse.lsp4j.jsonrpc.services.JsonDelegate;
import org.eclipse.lsp4j.services.LanguageServer;

public interface GroovyScriptLanguageServer extends LanguageServer {

    @JsonDelegate
    default GroovyScriptFeaturesService getGroovyScriptFeaturesService() {
        return null;
    }

}
