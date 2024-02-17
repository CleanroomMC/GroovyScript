package com.cleanroommc.groovyscript.server;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.GroovyScriptConfig;
import com.cleanroommc.groovyscript.server.provider.*;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;
import org.eclipse.lsp4j.jsonrpc.CompletableFutures;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.jsonrpc.messages.Either3;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.*;

import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class GroovyServer implements LanguageServer, LanguageClientAware, TextDocumentService, WorkspaceService {

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

        var generalClientCapabilities = clientCapabilities.getGeneral();
        var textDocumentClientCapabilities = clientCapabilities.getTextDocument();
        var windowClientCapabilities = clientCapabilities.getWindow();
        var workspaceClientCapabilities = clientCapabilities.getWorkspace();

        serverCapabilities.setColorProvider(true);

        var completion = new CompletionOptions();
        completion.setResolveProvider(true);
        completion.setTriggerCharacters(List.of()); // TODO: set
        serverCapabilities.setCompletionProvider(completion);

        serverCapabilities.setDeclarationProvider(true);

        serverCapabilities.setDefinitionProvider(true);
        serverCapabilities.setTypeDefinitionProvider(true);

        serverCapabilities.setFoldingRangeProvider(true);

        serverCapabilities.setDocumentHighlightProvider(true);

        serverCapabilities.setHoverProvider(true);

        var inlayHint = new InlayHintRegistrationOptions();
        inlayHint.setResolveProvider(true);
        serverCapabilities.setInlayHintProvider(inlayHint);

        serverCapabilities.setReferencesProvider(true);

        var rename = new RenameOptions();
        rename.setPrepareProvider(true);
        serverCapabilities.setRenameProvider(rename);

        var semanticTokensLegend = new SemanticTokensLegend();
        semanticTokensLegend.setTokenModifiers(List.of()); // TODO: set
        semanticTokensLegend.setTokenTypes(List.of()); // TODO: set
        var semanticTokens = new SemanticTokensWithRegistrationOptions(semanticTokensLegend, true, true);
        serverCapabilities.setSemanticTokensProvider(semanticTokens);

        var signatureHelp = new SignatureHelpOptions();
        signatureHelp.setTriggerCharacters(List.of()); // TODO: set
        serverCapabilities.setSignatureHelpProvider(signatureHelp);

        serverCapabilities.setDocumentSymbolProvider(true);
        serverCapabilities.setWorkspaceSymbolProvider(true);


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

    GroovyFiles files = new GroovyFiles(this);

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        files.didOpen(params);
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        files.didChange(params);
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        files.didClose(params);
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        files.didSave(params);
    }

    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams params) {

    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
        files.didChangeWatchedFiles(params);
    }

    private <U> CompletableFuture<U> mapCompile(Function<CancelChecker, U> function) {
        return CompletableFutures
                .computeAsync(cancelToken -> {
                    /* TODO: compile */
                    return cancelToken;
                })
                .thenApplyAsync(function);
    }

    @Override
    public CompletableFuture<List<ColorPresentation>> colorPresentation(ColorPresentationParams params) {
        return mapCompile(cancelToken -> ColorProvider.provide(params));
    }

    @Override
    public CompletableFuture<List<ColorInformation>> documentColor(DocumentColorParams params) {
        return mapCompile(cancelToken -> ColorProvider.provide(params));
    }

    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams params) {
        return mapCompile(cancelToken -> CompletionProvider.provide(params));
    }

    @Override
    public CompletableFuture<CompletionItem> resolveCompletionItem(CompletionItem unresolved) {
        return mapCompile(cancelToken -> CompletionProvider.provide(unresolved));
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> declaration(DeclarationParams params) {
        return mapCompile(cancelToken -> DeclarationProvider.provide(params));
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(DefinitionParams params) {
        return mapCompile(cancelToken -> DefinitionProvider.provide(params));
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> typeDefinition(TypeDefinitionParams params) {
        return mapCompile(cancelToken -> DefinitionProvider.provide(params));
    }

    @Override
    public CompletableFuture<List<FoldingRange>> foldingRange(FoldingRangeRequestParams params) {
        return mapCompile(cancelToken -> FoldingRangeProvider.provide(params));
    }

    @Override
    public CompletableFuture<List<? extends DocumentHighlight>> documentHighlight(DocumentHighlightParams params) {
        return mapCompile(cancelToken -> HighlightProvider.provide(params));
    }

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        return mapCompile(cancelToken -> HoverProvider.provide(params));
    }

    @Override
    public CompletableFuture<List<InlayHint>> inlayHint(InlayHintParams params) {
        return mapCompile(cancelToken -> InlayHintProvider.provide(params));
    }

    @Override
    public CompletableFuture<InlayHint> resolveInlayHint(InlayHint unresolved) {
        return mapCompile(cancelToken -> InlayHintProvider.provide(unresolved));
    }

    @Override
    public CompletableFuture<List<? extends Location>> references(ReferenceParams params) {
        return mapCompile(cancelToken -> ReferencesProvider.provide(params));
    }

    @Override
    public CompletableFuture<WorkspaceEdit> rename(RenameParams params) {
        return mapCompile(cancelToken -> RenameProvider.provide(params));
    }

    @Override
    public CompletableFuture<Either3<Range, PrepareRenameResult, PrepareRenameDefaultBehavior>> prepareRename(PrepareRenameParams params) {
        return mapCompile(cancelToken -> RenameProvider.provide(params));
    }

    @Override
    public CompletableFuture<SemanticTokens> semanticTokensFull(SemanticTokensParams params) {
        return mapCompile(cancelToken -> SemanticTokensProvider.provide(params));
    }

    @Override
    public CompletableFuture<SemanticTokens> semanticTokensRange(SemanticTokensRangeParams params) {
        return mapCompile(cancelToken -> SemanticTokensProvider.provide(params));
    }

    @Override
    public CompletableFuture<SignatureHelp> signatureHelp(SignatureHelpParams params) {
        return mapCompile(cancelToken -> SignatureHelpProvider.provide(params));
    }

    @Override
    public CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> documentSymbol(DocumentSymbolParams params) {
        return mapCompile(cancelToken -> SymbolProvider.provide(params));
    }

    @Override
    public CompletableFuture<Either<List<? extends SymbolInformation>, List<? extends WorkspaceSymbol>>> symbol(WorkspaceSymbolParams params) {
        return mapCompile(cancelToken -> SymbolProvider.provide(params));
    }
}
