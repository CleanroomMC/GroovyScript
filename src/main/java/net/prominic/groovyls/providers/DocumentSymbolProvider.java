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
import net.prominic.groovyls.util.GroovyLanguageServerUtils;
import net.prominic.groovyls.util.URIUtils;
import org.codehaus.groovy.ast.*;
import org.eclipse.lsp4j.DocumentSymbol;
import org.eclipse.lsp4j.SymbolInformation;
import org.eclipse.lsp4j.TextDocumentIdentifier;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DocumentSymbolProvider {

    private final ASTContext astContext;

    public DocumentSymbolProvider(ASTContext astContext) {
        this.astContext = astContext;
    }

    public CompletableFuture<List<Either<SymbolInformation, DocumentSymbol>>> provideDocumentSymbols(
            TextDocumentIdentifier textDocument) {
        URI uri = URIUtils.toUri(textDocument.getUri());
        List<ASTNode> nodes = astContext.getVisitor().getNodes(uri);
        List<Either<SymbolInformation, DocumentSymbol>> symbols = nodes.stream().filter(node -> {
            return node instanceof ClassNode || node instanceof MethodNode || node instanceof FieldNode
                   || node instanceof PropertyNode;
        }).map(node -> {
            if (node instanceof ClassNode) {
                ClassNode classNode = (ClassNode) node;
                return GroovyLanguageServerUtils.astNodeToSymbolInformation(classNode, uri, null);
            }
            ClassNode classNode = (ClassNode) GroovyASTUtils.getEnclosingNodeOfType(node, ClassNode.class, astContext);
            if (node instanceof MethodNode) {
                MethodNode methodNode = (MethodNode) node;
                return GroovyLanguageServerUtils.astNodeToSymbolInformation(methodNode, uri, classNode.getName());
            }
            if (node instanceof PropertyNode) {
                PropertyNode propNode = (PropertyNode) node;
                return GroovyLanguageServerUtils.astNodeToSymbolInformation(propNode, uri, classNode.getName());
            }
            if (node instanceof FieldNode) {
                FieldNode fieldNode = (FieldNode) node;
                return GroovyLanguageServerUtils.astNodeToSymbolInformation(fieldNode, uri, classNode.getName());
            }
            // this should never happen
            return null;
        }).filter(symbolInformation -> symbolInformation != null).map(node -> {
            return Either.<SymbolInformation, DocumentSymbol>forLeft(node);
        }).collect(Collectors.toList());
        return CompletableFuture.completedFuture(symbols);
    }
}