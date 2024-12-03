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

import com.cleanroommc.groovyscript.sandbox.FileUtil;
import net.prominic.groovyls.compiler.ILanguageServerContext;
import net.prominic.groovyls.config.ICompilationUnitFactory;
import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.*;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class GroovyLanguageServer<T extends GroovyServices> implements LanguageServer, LanguageClientAware {

    protected final T groovyServices;

    public GroovyLanguageServer(ICompilationUnitFactory compilationUnitFactory, ILanguageServerContext languageServerContext) {
        this.groovyServices = createGroovyServices(compilationUnitFactory, languageServerContext);
    }

    protected @NotNull T createGroovyServices(ICompilationUnitFactory compilationUnitFactory, ILanguageServerContext languageServerContext) {
        return (T) new GroovyServices(compilationUnitFactory, languageServerContext);
    }

    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        String rootUriString = params.getRootUri();
        if (rootUriString != null) {
            URI uri = URI.create(FileUtil.fixUriString(rootUriString));
            Path workspaceRoot = Paths.get(uri);
            groovyServices.setWorkspaceRoot(workspaceRoot);
        }

        CompletionOptions completionOptions = new CompletionOptions(false, Arrays.asList(".", "'", "\""));
        ServerCapabilities serverCapabilities = new ServerCapabilities();
        serverCapabilities.setCompletionProvider(completionOptions);
        serverCapabilities.setTextDocumentSync(TextDocumentSyncKind.Full);
        serverCapabilities.setDocumentSymbolProvider(true);
        serverCapabilities.setWorkspaceSymbolProvider(true);
        serverCapabilities.setDocumentSymbolProvider(true);
        serverCapabilities.setReferencesProvider(true);
        serverCapabilities.setDefinitionProvider(true);
        serverCapabilities.setTypeDefinitionProvider(true);
        serverCapabilities.setHoverProvider(true);
        serverCapabilities.setRenameProvider(true);
        SignatureHelpOptions signatureHelpOptions = new SignatureHelpOptions();
        signatureHelpOptions.setTriggerCharacters(Arrays.asList("(", ","));
        serverCapabilities.setSignatureHelpProvider(signatureHelpOptions);

        InitializeResult initializeResult = new InitializeResult(serverCapabilities);
        return CompletableFuture.completedFuture(initializeResult);
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        return CompletableFuture.completedFuture(new Object());
    }

    @Override
    public void exit() {}

    @Override
    public TextDocumentService getTextDocumentService() {
        return groovyServices;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return groovyServices;
    }

    @Override
    public void setTrace(SetTraceParams params) {}

    @Override
    public void connect(LanguageClient client) {
        groovyServices.connect(client);
    }
}
