package com.cleanroommc.groovyscript.server.provider;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.List;

/* TODO:
    - implement
 */
@SuppressWarnings("deprecation")
public class SymbolProvider {

    public static List<Either<SymbolInformation, DocumentSymbol>> provide(DocumentSymbolParams params) {
        return null;
    }

    public static Either<List<? extends SymbolInformation>, List<? extends WorkspaceSymbol>> provide(WorkspaceSymbolParams params) {
        return null;
    }
}
