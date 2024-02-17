package com.cleanroommc.groovyscript.server.provider;

import org.eclipse.lsp4j.CompletionItem;
import org.eclipse.lsp4j.CompletionList;
import org.eclipse.lsp4j.CompletionParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.List;

/* TODO:
    - implement
    - allow extensions from mods for bracket handlers
 */
public class CompletionProvider {

    public static Either<List<CompletionItem>, CompletionList> provide(CompletionParams params) {
        return null;
    }

    public static CompletionItem provide(CompletionItem unresolved) {
        return null;
    }
}
