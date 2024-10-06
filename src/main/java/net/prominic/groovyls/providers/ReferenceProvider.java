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
import org.codehaus.groovy.ast.ASTNode;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.TextDocumentIdentifier;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ReferenceProvider extends DocProvider {

    public ReferenceProvider(URI doc, ASTContext astContext) {
        super(doc, astContext);
    }

    public CompletableFuture<List<? extends Location>> provideReferences(TextDocumentIdentifier textDocument, Position position) {
        ASTNode offsetNode = astContext.getVisitor().getNodeAtLineAndColumn(doc, position.getLine(), position.getCharacter());
        if (offsetNode == null) {
            return CompletableFuture.completedFuture(Collections.emptyList());
        }

        List<ASTNode> references = GroovyASTUtils.getReferences(offsetNode, astContext);
        List<Location> locations = new ArrayList<>();
        for (ASTNode node : references) {
            Location loc = GroovyLSUtils.astNodeToLocation(node, astContext.getVisitor().getURI(node));
            if (loc != null) {
                locations.add(loc);
            }
        }

        return CompletableFuture.completedFuture(locations);
    }

}
