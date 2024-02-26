////////////////////////////////////////////////////////////////////////////////
// Copyright 2022 Prominic.NET, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License
//
// Author: Prominic.NET, Inc.
// No warranty of merchantability or fitness of any kind.
// Use this software at your own risk.
////////////////////////////////////////////////////////////////////////////////
package net.prominic.groovyls;

import com.cleanroommc.groovyscript.GroovyScript;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.prominic.groovyls.compiler.ILanguageServerContext;
import net.prominic.groovyls.compiler.ast.ASTContext;
import net.prominic.groovyls.compiler.ast.ASTNodeVisitor;
import net.prominic.groovyls.compiler.control.GroovyLSCompilationUnit;
import net.prominic.groovyls.config.ICompilationUnitFactory;
import net.prominic.groovyls.providers.*;
import net.prominic.groovyls.util.GroovyLanguageServerUtils;
import net.prominic.groovyls.util.URIUtils;
import net.prominic.lsp.utils.Positions;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.messages.Message;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.syntax.SyntaxException;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageClientAware;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GroovyServices implements TextDocumentService, WorkspaceService, LanguageClientAware {

    private static final Pattern PATTERN_CONSTRUCTOR_CALL = Pattern.compile(".*new \\w*$");

    private LanguageClient languageClient;

    private Path workspaceRoot;
    private ICompilationUnitFactory compilationUnitFactory;
    private final ILanguageServerContext languageServerContext;
    private Map<URI, List<Diagnostic>> prevDiagnosticsByFile;

    public GroovyServices(ICompilationUnitFactory factory, ILanguageServerContext languageServerContext) {
        compilationUnitFactory = factory;
        this.languageServerContext = languageServerContext;
    }

    public void setWorkspaceRoot(Path workspaceRoot) {
        this.workspaceRoot = workspaceRoot;
        compilationUnitFactory.invalidateCompilationUnit();
    }

    @Override
    public void connect(LanguageClient client) {
        languageClient = client;
    }

    // --- NOTIFICATIONS

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        languageServerContext.getFileContentsTracker().didOpen(params);
        URI uri = URIUtils.toUri(params.getTextDocument().getUri());

        var unit = compilationUnitFactory.create(workspaceRoot, uri);

        compileAndVisitAST(unit, uri);
    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        languageServerContext.getFileContentsTracker().didChange(params);
        URI uri = URIUtils.toUri(params.getTextDocument().getUri());

        var unit = compilationUnitFactory.create(workspaceRoot, uri);

        compileAndVisitAST(unit, uri);
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        languageServerContext.getFileContentsTracker().didClose(params);
        URI uri = URIUtils.toUri(params.getTextDocument().getUri());

        var unit = compilationUnitFactory.create(workspaceRoot, uri);

        compileAndVisitAST(unit, uri);
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        // nothing to handle on save at this time
    }

    @Override
    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
        Set<URI> urisWithChanges = params.getChanges().stream().map(fileEvent -> URIUtils.toUri(fileEvent.getUri()))
                .collect(Collectors.toSet());

        for (URI uri : urisWithChanges) {
            var unit = compilationUnitFactory.create(workspaceRoot, uri);

            compileAndVisitAST(unit, uri);
        }
    }

    @Override
    public void didChangeConfiguration(DidChangeConfigurationParams params) {
        if (!(params.getSettings() instanceof JsonObject)) {
            return;
        }
        JsonObject settings = (JsonObject) params.getSettings();
        this.updateClasspath(settings);
    }

    private void updateClasspath(JsonObject settings) {
        List<String> classpathList = new ArrayList<>();

        if (settings.has("groovy") && settings.get("groovy").isJsonObject()) {
            JsonObject groovy = settings.get("groovy").getAsJsonObject();
            if (groovy.has("classpath") && groovy.get("classpath").isJsonArray()) {
                JsonArray classpath = groovy.get("classpath").getAsJsonArray();
                classpath.forEach(element -> {
                    classpathList.add(element.getAsString());
                });
            }
        }

        if (!classpathList.equals(compilationUnitFactory.getAdditionalClasspathList())) {
            compilationUnitFactory.setAdditionalClasspathList(classpathList);
        }
    }

    // --- REQUESTS

    @Override
    public CompletableFuture<Hover> hover(HoverParams params) {
        URI uri = URIUtils.toUri(params.getTextDocument().getUri());
        var unit = compilationUnitFactory.create(workspaceRoot, uri);

        var visitor = compileAndVisitAST(unit, uri);

        if (visitor == null) {
            return CompletableFuture.completedFuture(null);
        }

        HoverProvider provider = new HoverProvider(new ASTContext(visitor, languageServerContext));
        return provider.provideHover(params.getTextDocument(), params.getPosition());
    }

    @Override
    public CompletableFuture<Either<List<CompletionItem>, CompletionList>> completion(CompletionParams params) {
        TextDocumentIdentifier textDocument = params.getTextDocument();
        Position position = params.getPosition();
        URI uri = URIUtils.toUri(textDocument.getUri());
        var unit = compilationUnitFactory.create(workspaceRoot, uri);

        var visitor = compileAndVisitAST(unit, uri);

        String originalSource = null;
        ASTNode offsetNode = visitor.getNodeAtLineAndColumn(uri, position.getLine(), position.getCharacter());
        if (offsetNode == null) {
            originalSource = languageServerContext.getFileContentsTracker().getContents(uri);
            VersionedTextDocumentIdentifier versionedTextDocument = new VersionedTextDocumentIdentifier(
                    textDocument.getUri(), 1);
            int offset = Positions.getOffset(originalSource, position);
            String lineBeforeOffset = originalSource.substring(offset - position.getCharacter(), offset);
            Matcher matcher = PATTERN_CONSTRUCTOR_CALL.matcher(lineBeforeOffset);
            TextDocumentContentChangeEvent changeEvent = null;
            if (matcher.matches()) {
                changeEvent = new TextDocumentContentChangeEvent(new Range(position, position), 0, "a()");
            } else {
                changeEvent = new TextDocumentContentChangeEvent(new Range(position, position), 0, "a");
            }
            DidChangeTextDocumentParams didChangeParams = new DidChangeTextDocumentParams(versionedTextDocument,
                                                                                          Collections.singletonList(changeEvent));
            // if the offset node is null, there is probably a syntax error.
            // a completion request is usually triggered by the . character, and
            // if there is no property name after the dot, it will cause a syntax
            // error.
            // this hack adds a placeholder property name in the hopes that it
            // will correctly create a PropertyExpression to use for completion.
            // we'll restore the original text after we're done handling the
            // completion request.
            didChange(didChangeParams);
        }

        CompletableFuture<Either<List<CompletionItem>, CompletionList>> result = null;
        try {
            CompletionProvider provider = new CompletionProvider(new ASTContext(visitor, languageServerContext));
            result = provider.provideCompletion(params.getTextDocument(), params.getPosition(), params.getContext());
        } finally {
            if (originalSource != null) {
                VersionedTextDocumentIdentifier versionedTextDocument = new VersionedTextDocumentIdentifier(
                        textDocument.getUri(), 1);
                TextDocumentContentChangeEvent changeEvent = new TextDocumentContentChangeEvent(null, 0,
                                                                                                originalSource);
                DidChangeTextDocumentParams didChangeParams = new DidChangeTextDocumentParams(versionedTextDocument,
                                                                                              Collections.singletonList(changeEvent));
                didChange(didChangeParams);
            }
        }

        return result;
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(
            DefinitionParams params) {
        URI uri = URIUtils.toUri(params.getTextDocument().getUri());
        var unit = compilationUnitFactory.create(workspaceRoot, uri);

        var visitor = compileAndVisitAST(unit, uri);

        DefinitionProvider provider = new DefinitionProvider(new ASTContext(visitor, languageServerContext));
        return provider.provideDefinition(params.getTextDocument(), params.getPosition());
    }

    @Override
    public CompletableFuture<SignatureHelp> signatureHelp(SignatureHelpParams params) {
        TextDocumentIdentifier textDocument = params.getTextDocument();
        Position position = params.getPosition();
        URI uri = URIUtils.toUri(textDocument.getUri());
        var unit = compilationUnitFactory.create(workspaceRoot, uri);

        var visitor = compileAndVisitAST(unit, uri);

        String originalSource = null;
        ASTNode offsetNode = visitor.getNodeAtLineAndColumn(uri, position.getLine(), position.getCharacter());
        if (offsetNode == null) {
            originalSource = languageServerContext.getFileContentsTracker().getContents(uri);
            VersionedTextDocumentIdentifier versionedTextDocument = new VersionedTextDocumentIdentifier(
                    textDocument.getUri(), 1);
            TextDocumentContentChangeEvent changeEvent = new TextDocumentContentChangeEvent(
                    new Range(position, position), 0, ")");
            DidChangeTextDocumentParams didChangeParams = new DidChangeTextDocumentParams(versionedTextDocument,
                                                                                          Collections.singletonList(changeEvent));
            // if the offset node is null, there is probably a syntax error.
            // a signature help request is usually triggered by the ( character,
            // and if there is no matching ), it will cause a syntax error.
            // this hack adds a placeholder ) character in the hopes that it
            // will correctly create a ArgumentListExpression to use for
            // signature help.
            // we'll restore the original text after we're done handling the
            // signature help request.
            didChange(didChangeParams);
        }

        try {
            SignatureHelpProvider provider = new SignatureHelpProvider(new ASTContext(visitor, languageServerContext));
            return provider.provideSignatureHelp(params.getTextDocument(), params.getPosition());
        } finally {
            if (originalSource != null) {
                VersionedTextDocumentIdentifier versionedTextDocument = new VersionedTextDocumentIdentifier(
                        textDocument.getUri(), 1);
                TextDocumentContentChangeEvent changeEvent = new TextDocumentContentChangeEvent(null, 0,
                                                                                                originalSource);
                DidChangeTextDocumentParams didChangeParams = new DidChangeTextDocumentParams(versionedTextDocument,
                                                                                              Collections.singletonList(changeEvent));
                didChange(didChangeParams);
            }
        }
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> typeDefinition(
            TypeDefinitionParams params) {
        URI uri = URIUtils.toUri(params.getTextDocument().getUri());
        var unit = compilationUnitFactory.create(workspaceRoot, uri);

        var visitor = compileAndVisitAST(unit, uri);

        TypeDefinitionProvider provider = new TypeDefinitionProvider(new ASTContext(visitor, languageServerContext));
        return provider.provideTypeDefinition(params.getTextDocument(), params.getPosition());
    }

    @Override
    public CompletableFuture<List<? extends Location>> references(ReferenceParams params) {
        URI uri = URIUtils.toUri(params.getTextDocument().getUri());
        var unit = compilationUnitFactory.create(workspaceRoot, uri);

        var visitor = compileAndVisitAST(unit, uri);

        ReferenceProvider provider = new ReferenceProvider(new ASTContext(visitor, languageServerContext));
        return provider.provideReferences(params.getTextDocument(), params.getPosition());
    }

    @Override
    public CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> documentSymbol(
            DocumentSymbolParams params) {
        URI uri = URIUtils.toUri(params.getTextDocument().getUri());
        var unit = compilationUnitFactory.create(workspaceRoot, uri);

        var visitor = compileAndVisitAST(unit, uri);

        DocumentSymbolProvider provider = new DocumentSymbolProvider(new ASTContext(visitor, languageServerContext));
        return provider.provideDocumentSymbols(params.getTextDocument());
    }

    @Override
    public CompletableFuture<Either<List<? extends SymbolInformation>, List<? extends WorkspaceSymbol>>> symbol(WorkspaceSymbolParams params) {
        var unit = compilationUnitFactory.create(workspaceRoot, null);

        var visitor = compileAndVisitAST(unit, null);

        WorkspaceSymbolProvider provider = new WorkspaceSymbolProvider(new ASTContext(visitor, languageServerContext));
        return provider.provideWorkspaceSymbols(params.getQuery()).thenApply(Either::forLeft);
    }

    @Override
    public CompletableFuture<WorkspaceEdit> rename(RenameParams params) {
        URI uri = URIUtils.toUri(params.getTextDocument().getUri());
        var unit = compilationUnitFactory.create(workspaceRoot, uri);

        var visitor = compileAndVisitAST(unit, uri);

        RenameProvider provider = new RenameProvider(new ASTContext(visitor, languageServerContext), languageServerContext.getFileContentsTracker());
        return provider.provideRename(params);
    }

    private @Nullable ASTNodeVisitor compileAndVisitAST(GroovyLSCompilationUnit compilationUnit, URI context) {
        try {
            return compilationUnit.recompileAndVisitASTIfContextChanged(context);
        } catch (GroovyBugError | Exception e) {
            GroovyScript.LOGGER.error("Unexpected exception in language server when compiling Groovy.", e);
        } finally {
            Set<PublishDiagnosticsParams> diagnostics = handleErrorCollector(compilationUnit.getErrorCollector());
            diagnostics.stream().forEach(languageClient::publishDiagnostics);
        }

        return null;
    }

    private Set<PublishDiagnosticsParams> handleErrorCollector(ErrorCollector collector) {
        Map<URI, List<Diagnostic>> diagnosticsByFile = new HashMap<>();

        List<? extends Message> errors = collector.getErrors();
        if (errors != null) {
            errors.stream().filter((Object message) -> message instanceof SyntaxErrorMessage)
                    .forEach((Object message) -> {
                        SyntaxErrorMessage syntaxErrorMessage = (SyntaxErrorMessage) message;
                        SyntaxException cause = syntaxErrorMessage.getCause();
                        Range range = GroovyLanguageServerUtils.syntaxExceptionToRange(cause);
                        Diagnostic diagnostic = new Diagnostic();
                        diagnostic.setRange(range);
                        diagnostic.setSeverity(DiagnosticSeverity.Error);
                        diagnostic.setMessage(cause.getMessage());
                        URI uri = Paths.get(cause.getSourceLocator()).toUri();
                        diagnosticsByFile.computeIfAbsent(uri, (key) -> new ArrayList<>()).add(diagnostic);
                    });
        }

        Set<PublishDiagnosticsParams> result = diagnosticsByFile.entrySet().stream()
                .map(entry -> new PublishDiagnosticsParams(entry.getKey().toString(), entry.getValue()))
                .collect(Collectors.toSet());

        if (prevDiagnosticsByFile != null) {
            for (URI key : prevDiagnosticsByFile.keySet()) {
                if (!diagnosticsByFile.containsKey(key)) {
                    // send an empty list of diagnostics for files that had
                    // diagnostics previously or they won't be cleared
                    result.add(new PublishDiagnosticsParams(key.toString(), new ArrayList<>()));
                }
            }
        }
        prevDiagnosticsByFile = diagnosticsByFile;
        return result;
    }
}