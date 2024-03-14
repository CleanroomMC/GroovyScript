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

import com.cleanroommc.groovyscript.GroovyScript;
import net.prominic.groovyls.compiler.ast.ASTContext;
import net.prominic.groovyls.compiler.util.GroovyASTUtils;
import net.prominic.groovyls.util.GroovyNodeToStringUtils;
import net.prominic.groovyls.util.URIUtils;
import org.codehaus.groovy.ast.*;
import org.eclipse.lsp4j.*;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

public class HoverProvider {

    private final ASTContext astContext;

    public HoverProvider(ASTContext astContext) {
        this.astContext = astContext;
    }

    public CompletableFuture<Hover> provideHover(TextDocumentIdentifier textDocument, Position position) {
        URI uri = URIUtils.toUri(textDocument.getUri());
        ASTNode offsetNode = astContext.getVisitor().getNodeAtLineAndColumn(uri, position.getLine(), position.getCharacter());
        if (offsetNode == null) {
            return CompletableFuture.completedFuture(null);
        }

        ASTNode definitionNode = GroovyASTUtils.getDefinition(offsetNode, false, astContext);
        if (definitionNode == null) {
            return CompletableFuture.completedFuture(null);
        }

        String content = getContent(definitionNode);
        if (content == null) {
            return CompletableFuture.completedFuture(null);
        }

        String documentation = null;
        if (definitionNode instanceof AnnotatedNode annotatedNode) {
            documentation = astContext.getLanguageServerContext().getDocumentationFactory().getDocumentation(annotatedNode, astContext);
        }

        StringBuilder contentsBuilder = new StringBuilder();
        contentsBuilder.append("```groovy\n");
        contentsBuilder.append(content);
        contentsBuilder.append("\n```");
        if (documentation != null) {
            contentsBuilder.append("\n\n---\n\n");
            contentsBuilder.append(documentation);
        }

        MarkupContent contents = new MarkupContent();
        contents.setKind(MarkupKind.MARKDOWN);
        contents.setValue(contentsBuilder.toString());
        Hover hover = new Hover();
        hover.setContents(contents);
        return CompletableFuture.completedFuture(hover);
    }

    private String getContent(ASTNode hoverNode) {
        if (hoverNode instanceof ClassNode) {
            ClassNode classNode = (ClassNode) hoverNode;
            return GroovyNodeToStringUtils.classToString(classNode, astContext);
        } else if (hoverNode instanceof MethodNode) {
            MethodNode methodNode = (MethodNode) hoverNode;
            return GroovyNodeToStringUtils.methodToString(methodNode, astContext);
        } else if (hoverNode instanceof Variable) {
            Variable varNode = (Variable) hoverNode;
            return GroovyNodeToStringUtils.variableToString(varNode, astContext);
        } else {
            GroovyScript.LOGGER.warn("*** hover not available for node: {}", hoverNode);
        }
        return null;
    }
}