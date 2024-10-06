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
package net.prominic.groovyls.providers;

import net.prominic.groovyls.compiler.ast.ASTContext;
import net.prominic.groovyls.compiler.util.GroovyASTUtils;
import net.prominic.groovyls.util.GroovyLSUtils;
import org.codehaus.groovy.ast.*;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DocumentSymbolProvider extends DocProvider {

    public DocumentSymbolProvider(URI doc, ASTContext astContext) {
        super(doc, astContext);
    }

    public CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> provideDocumentSymbolsFuture(
                                                                                                           TextDocumentIdentifier textDocument) {
        return future(provideDocumentSymbols(textDocument));
    }

    public List<Either<SymbolInformation, DocumentSymbol>> provideDocumentSymbols(TextDocumentIdentifier textDocument) {
        List<Either<SymbolInformation, DocumentSymbol>> symbols = new ArrayList<>();
        for (ASTNode node : astContext.getVisitor().getNodes(doc)) {
            DocumentSymbol symbol = null;
            if (node instanceof ClassNode classNode) {
                symbol = GroovyLSUtils.astNodeToSymbolInformation(classNode, doc, null);
            } else {
                ClassNode classNode = GroovyASTUtils.getEnclosingNodeOfType(node, ClassNode.class, astContext);
                if (classNode == null) continue;
                if (node instanceof MethodNode methodNode) {
                    symbol = GroovyLSUtils.astNodeToSymbolInformation(methodNode, doc, classNode.getName());
                } else if (node instanceof PropertyNode propNode) {
                    symbol = GroovyLSUtils.astNodeToSymbolInformation(propNode, doc, classNode.getName());
                } else if (node instanceof FieldNode fieldNode) {
                    symbol = GroovyLSUtils.astNodeToSymbolInformation(fieldNode, doc, classNode.getName());
                }
            }
            if (symbol != null) symbols.add(Either.forRight(symbol));
        }
        return symbols;
    }

}
