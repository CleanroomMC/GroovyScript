package com.cleanroommc.groovyscript.server.provider;

import org.eclipse.lsp4j.DefinitionParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.TypeDefinitionParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.List;

/* TODO:
    - implement
 */
public class DefinitionProvider {

    public static Either<List<? extends Location>, List<? extends LocationLink>> provide(DefinitionParams params) {
        return null;
    }

    public static Either<List<? extends Location>, List<? extends LocationLink>> provide(TypeDefinitionParams params) {
        return null;
    }
}
