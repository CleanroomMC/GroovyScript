package com.cleanroommc.groovyscript.server.provider;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either3;

/* TODO:
    - implement
 */
public class RenameProvider {

    public static WorkspaceEdit provide(RenameParams params) {
        return null;
    }

    public static Either3<Range, PrepareRenameResult, PrepareRenameDefaultBehavior> provide(PrepareRenameParams params) {return null;}
}
