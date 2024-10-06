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
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.SymbolKind;
import org.eclipse.lsp4j.WorkspaceSymbol;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WorkspaceSymbolProvider {

    private final ASTContext astContext;

    public WorkspaceSymbolProvider(ASTContext astContext) {
        this.astContext = astContext;
    }

    public CompletableFuture<List<WorkspaceSymbol>> provideWorkspaceSymbols(String query) {
        String lowerCaseQuery = query.toLowerCase();
        List<WorkspaceSymbol> symbols = new ArrayList<>();
        for (ASTNode node : astContext.getVisitor().getNodes()) {
            String name = null;
            String parent = null;
            if (node instanceof ClassNode classNode) {
                name = classNode.getName();
            } else {
                ClassNode classNode = GroovyASTUtils.getEnclosingNodeOfType(node, ClassNode.class, astContext);
                if (classNode != null) parent = classNode.getName();
                if (node instanceof MethodNode methodNode) {
                    name = methodNode.getName();
                } else if (node instanceof FieldNode fieldNode) {
                    name = fieldNode.getName();
                } else if (node instanceof PropertyNode propNode) {
                    name = propNode.getName();
                }
            }
            if (name == null || !name.toLowerCase().contains(lowerCaseQuery)) continue;
            Range range = GroovyLSUtils.astNodeToRange(node);
            if (range == null) continue;
            SymbolKind kind = GroovyLSUtils.astNodeToSymbolKind(node);
            URI uri = astContext.getVisitor().getURI(node);
            symbols.add(new WorkspaceSymbol(name, kind, Either.forLeft(new Location(uri.toString(), range)), parent));
        }
        return CompletableFuture.completedFuture(symbols);
    }

}
