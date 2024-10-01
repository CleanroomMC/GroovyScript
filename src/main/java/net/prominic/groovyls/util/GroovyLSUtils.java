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
package net.prominic.groovyls.util;

import com.cleanroommc.groovyscript.core.mixin.groovy.ModuleNodeAccessor;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.syntax.SyntaxException;
import org.eclipse.lsp4j.*;

import java.net.URI;

public class GroovyLSUtils {

    /**
     * Converts a Groovy position to a LSP position. May return null if the Groovy line is -1
     */
    public static Position createGroovyPosition(int line, int column) {
        if (line < 0) return null;
        if (column < 0) {
            column = 0;
        } else if (column > 0) column--;
        if (line > 0) line--;
        return new Position(line, column);
    }

    public static Range syntaxExceptionToRange(SyntaxException exception) {
        return rangeOf(exception.getStartLine(), exception.getStartColumn(), exception.getEndLine(), exception.getEndColumn());
    }

    public static Range rangeOf(int startLine, int startCol, int endLine, int endCol) {
        Position start = createGroovyPosition(startLine, startCol);
        if (start == null) return null;
        Position end = createGroovyPosition(endLine, endCol);
        if (end == null) end = start;
        return new Range(start, end);
    }

    /**
     * Converts a Groovy AST node to an LSP range. May return null if the node's start line is -1
     */
    public static Range astNodeToRange(ASTNode node) {
        return rangeOf(node.getLineNumber(), node.getColumnNumber(), node.getLastLineNumber(), node.getLastColumnNumber());
    }

    public static Range astNodeToRange(ASTNode start, ASTNode end) {
        return rangeOf(start.getLineNumber(), start.getColumnNumber(), end.getLastLineNumber(), end.getLastColumnNumber());
    }

    public static CompletionItemKind astNodeToCompletionItemKind(ASTNode node) {
        if (node instanceof ClassNode classNode) {
            if (classNode.isInterface()) {
                return CompletionItemKind.Interface;
            } else if (classNode.isEnum()) {
                return CompletionItemKind.Enum;
            }
            return CompletionItemKind.Class;
        } else if (node instanceof MethodNode) {
            return CompletionItemKind.Method;
        } else if (node instanceof Variable) {
            if (node instanceof FieldNode || node instanceof PropertyNode) {
                return CompletionItemKind.Field;
            }
            return CompletionItemKind.Variable;
        }
        return CompletionItemKind.Property;
    }

    public static SymbolKind astNodeToSymbolKind(ASTNode node) {
        if (node instanceof ClassNode classNode) {
            if (classNode.isInterface()) {
                return SymbolKind.Interface;
            } else if (classNode.isEnum()) {
                return SymbolKind.Enum;
            }
            return SymbolKind.Class;
        } else if (node instanceof MethodNode) {
            return SymbolKind.Method;
        } else if (node instanceof Variable) {
            if (node instanceof FieldNode || node instanceof PropertyNode) {
                return SymbolKind.Field;
            }
            return SymbolKind.Variable;
        }
        return SymbolKind.Property;
    }

    /**
     * Converts a Groovy AST node to an LSP location. May return null if the node's start line is -1
     */
    public static Location astNodeToLocation(ASTNode node, URI uri) {
        Range range = astNodeToRange(node);
        if (range == null) return null;
        return new Location(uri.toString(), range);
    }

    public static DocumentSymbol astNodeToSymbolInformation(ClassNode node, URI uri, String parentName) {
        Range location = astNodeToRange(node);
        if (location == null) return null;
        SymbolKind symbolKind = astNodeToSymbolKind(node);
        // TODO add method and field children
        return new DocumentSymbol(node.getName(), symbolKind, location, location, parentName);
    }

    public static DocumentSymbol astNodeToSymbolInformation(MethodNode node, URI uri, String parentName) {
        Range location = astNodeToRange(node);
        if (location == null) return null;
        SymbolKind symbolKind = astNodeToSymbolKind(node);
        return new DocumentSymbol(node.getName(), symbolKind, location, location, parentName);
    }

    public static DocumentSymbol astNodeToSymbolInformation(Variable node, URI uri, String parentName) {
        if (!(node instanceof ASTNode astVar)) {
            // DynamicVariable isn't an ASTNode
            return null;
        }
        Range location = astNodeToRange(astVar);
        if (location == null) return null;
        SymbolKind symbolKind = astNodeToSymbolKind(astVar);
        return new DocumentSymbol(node.getName(), symbolKind, location, location, parentName);
    }

    public static boolean hasImport(ModuleNode module, String className) {
        if (className == null) return false;
        for (ImportNode in : ((ModuleNodeAccessor) module).getModifiableImports()) {
            if (in.getType() != null && in.getType().getName().equals(className)) {
                return true;
            }
        }
        return false;
    }
}
