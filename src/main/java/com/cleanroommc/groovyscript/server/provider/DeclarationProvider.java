package com.cleanroommc.groovyscript.server.provider;

import org.eclipse.lsp4j.DeclarationParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.List;

public class DeclarationProvider {

    public static Either<List<? extends Location>, List<? extends LocationLink>> provide(DeclarationParams params) {
        return null;
    }
}
