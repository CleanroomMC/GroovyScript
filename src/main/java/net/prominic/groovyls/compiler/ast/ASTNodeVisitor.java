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
package net.prominic.groovyls.compiler.ast;

import com.cleanroommc.groovyscript.helper.BetterList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.prominic.groovyls.util.GroovyLanguageServerUtils;
import net.prominic.lsp.utils.Positions;
import net.prominic.lsp.utils.Ranges;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;
import org.codehaus.groovy.classgen.BytecodeExpression;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.SourceUnit;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class ASTNodeVisitor extends ClassCodeVisitorSupport {

    private static class ASTLookupKey {

        public ASTLookupKey(ASTNode node) {
            this.node = node;
        }

        private final ASTNode node;

        @Override
        public boolean equals(Object o) {
            // some ASTNode subclasses, like ClassNode, override equals() with
            // comparisons that are not strict. we need strict.
            return o instanceof ASTLookupKey other && node == other.node;
        }

        @Override
        public int hashCode() {
            return node.hashCode();
        }
    }

    private static class ASTNodeLookupData {

        public ASTNode parent;
        public URI uri;
    }

    private SourceUnit sourceUnit;

    @Override
    protected SourceUnit getSourceUnit() {
        return sourceUnit;
    }

    private final BetterList<ASTNode> stack = new BetterList<>();
    private final Map<URI, List<ASTNode>> nodesByURI = new Object2ObjectOpenHashMap<>();
    private final Map<URI, List<ClassNode>> classNodesByURI = new Object2ObjectOpenHashMap<>();
    private final Map<ASTLookupKey, ASTNodeLookupData> lookup = new Object2ObjectOpenHashMap<>();

    private void pushASTNode(ASTNode node) {
        if (!(node instanceof AnnotatedNode an && an.isSynthetic())) {
            URI uri = sourceUnit.getSource().getURI();
            nodesByURI.get(uri).add(node);

            ASTNodeLookupData data = new ASTNodeLookupData();
            data.uri = uri;
            data.parent = stack.peekLast();
            lookup.put(new ASTLookupKey(node), data);
        }
        stack.add(node);
    }

    private void popASTNode() {
        stack.pop();
    }

    public List<ClassNode> getClassNodes() {
        List<ClassNode> result = new ArrayList<>();
        for (List<ClassNode> nodes : classNodesByURI.values()) {
            result.addAll(nodes);
        }
        return result;
    }

    public List<ASTNode> getNodes() {
        List<ASTNode> result = new ArrayList<>();
        for (List<ASTNode> nodes : nodesByURI.values()) {
            result.addAll(nodes);
        }
        return result;
    }

    public List<ASTNode> getNodes(URI uri) {
        List<ASTNode> nodes = nodesByURI.get(uri);
        if (nodes == null) {
            return Collections.emptyList();
        }
        return nodes;
    }

    public ASTNode getNodeAtLineAndColumn(URI uri, int line, int column) {
        Position position = new Position(line, column);
        Map<ASTNode, Range> nodeToRange = new HashMap<>();
        List<ASTNode> nodes = nodesByURI.get(uri);
        if (nodes == null) {
            return null;
        }
        List<ASTNode> foundNodes = nodes.stream().filter(node -> {
            if (node.getLineNumber() == -1) {
                // can't be the offset node if it has no position
                // also, do this first because it's the fastest comparison
                return false;
            }
            Range range = GroovyLanguageServerUtils.astNodeToRange(node);
            if (range == null) {
                return false;
            }
            boolean result = Ranges.contains(range, position);
            if (result) {
                // save the range object to avoid creating it again when we
                // sort the nodes
                nodeToRange.put(node, range);
            }
            return result;
        }).sorted((n1, n2) -> {
            int result = Positions.COMPARATOR.reversed().compare(nodeToRange.get(n1).getStart(),
                                                                 nodeToRange.get(n2).getStart());
            if (result != 0) {
                return result;
            }
            result = Positions.COMPARATOR.compare(nodeToRange.get(n1).getEnd(), nodeToRange.get(n2).getEnd());
            if (result != 0) {
                return result;
            }
            // n1 and n2 have the same range
            if (contains(n1, n2)) {
                if (n1 instanceof ClassNode && n2 instanceof ConstructorNode) {
                    return -1;
                }
                return 1;
            } else if (contains(n2, n1)) {
                if (n2 instanceof ClassNode && n1 instanceof ConstructorNode) {
                    return 1;
                }
                return -1;
            }
            return 0;
        }).collect(Collectors.toList());
        if (foundNodes.isEmpty()) {
            return null;
        }
        return foundNodes.get(0);
    }

    public ASTNode getParent(ASTNode child) {
        if (child == null) {
            return null;
        }
        ASTNodeLookupData data = lookup.get(new ASTLookupKey(child));
        if (data == null) {
            return null;
        }
        return data.parent;
    }

    public boolean contains(ASTNode ancestor, ASTNode descendant) {
        ASTNode current = getParent(descendant);
        while (current != null) {
            if (current.equals(ancestor)) {
                return true;
            }
            current = getParent(current);
        }
        return false;
    }

    public URI getURI(ASTNode node) {
        ASTNodeLookupData data = lookup.get(new ASTLookupKey(node));
        if (data == null) {
            return null;
        }
        return data.uri;
    }

    public void visitCompilationUnit(CompilationUnit unit) {
        nodesByURI.clear();
        classNodesByURI.clear();
        lookup.clear();
        unit.iterator().forEachRemaining(this::visitSourceUnit);
    }

    public void visitCompilationUnit(CompilationUnit unit, Collection<URI> uris) {
        uris.forEach(uri -> {
            // clear all old nodes so that they may be replaced
            List<ASTNode> nodes = nodesByURI.remove(uri);
            if (nodes != null) {
                nodes.forEach(node -> {
                    lookup.remove(new ASTLookupKey(node));
                });
            }
            classNodesByURI.remove(uri);
        });
        unit.iterator().forEachRemaining(sourceUnit -> {
            URI uri = sourceUnit.getSource().getURI();
            if (!uris.contains(uri)) {
                return;
            }
            visitSourceUnit(sourceUnit);
        });
    }

    public void visitSourceUnit(SourceUnit unit) {
        sourceUnit = unit;
        URI uri = sourceUnit.getSource().getURI();
        nodesByURI.put(uri, new ArrayList<>());
        classNodesByURI.put(uri, new ArrayList<>());
        stack.clear();
        ModuleNode moduleNode = unit.getAST();
        if (moduleNode != null) {
            visitModule(moduleNode);
        }
        sourceUnit = null;
        stack.clear();
    }

    public void visitModule(ModuleNode node) {
        pushASTNode(node);
        try {
            node.getClasses().forEach(this::visitClass);
        } finally {
            popASTNode();
        }
    }

    // GroovyClassVisitor

    public void visitClass(ClassNode node) {
        URI uri = sourceUnit.getSource().getURI();
        classNodesByURI.get(uri).add(node);
        pushASTNode(node);
        try {
            ClassNode unresolvedSuperClass = node.getUnresolvedSuperClass();
            if (unresolvedSuperClass != null && unresolvedSuperClass.getLineNumber() != -1) {
                pushASTNode(unresolvedSuperClass);
                popASTNode();
            }
            for (ClassNode unresolvedInterface : node.getUnresolvedInterfaces()) {
                if (unresolvedInterface.getLineNumber() == -1) {
                    continue;
                }
                pushASTNode(unresolvedInterface);
                popASTNode();
            }
            super.visitClass(node);
        } finally {
            popASTNode();
        }
    }

    @Override
    public void visitImports(ModuleNode node) {
        if (node != null) {
            for (ImportNode importNode : node.getImports()) {
                pushASTNode(importNode);
                visitAnnotations(importNode);
                importNode.visit(this);
                popASTNode();
            }
            for (ImportNode importStarNode : node.getStarImports()) {
                pushASTNode(importStarNode);
                visitAnnotations(importStarNode);
                importStarNode.visit(this);
                popASTNode();
            }
            for (ImportNode importStaticNode : node.getStaticImports().values()) {
                pushASTNode(importStaticNode);
                visitAnnotations(importStaticNode);
                importStaticNode.visit(this);
                popASTNode();
            }
            for (ImportNode importStaticStarNode : node.getStaticStarImports().values()) {
                pushASTNode(importStaticStarNode);
                visitAnnotations(importStaticStarNode);
                importStaticStarNode.visit(this);
                popASTNode();
            }
        }
    }

    public void visitConstructor(ConstructorNode node) {
        pushASTNode(node);
        try {
            super.visitConstructor(node);
            for (Parameter parameter : node.getParameters()) {
                visitParameter(parameter);
            }
        } finally {
            popASTNode();
        }
    }

    public void visitMethod(MethodNode node) {
        pushASTNode(node);
        try {
            super.visitMethod(node);
            for (Parameter parameter : node.getParameters()) {
                visitParameter(parameter);
            }
        } finally {
            popASTNode();
        }
    }

    protected void visitParameter(Parameter node) {
        // only add node to lookup map
        pushASTNode(node);
        popASTNode();
    }

    public void visitField(FieldNode node) {
        pushASTNode(node);
        try {
            super.visitField(node);
        } finally {
            popASTNode();
        }
    }

    public void visitProperty(PropertyNode node) {
        pushASTNode(node);
        try {
            super.visitProperty(node);
        } finally {
            popASTNode();
        }
    }

    // GroovyCodeVisitor

    public void visitBlockStatement(BlockStatement node) {
        pushASTNode(node);
        try {
            super.visitBlockStatement(node);
        } finally {
            popASTNode();
        }
    }

    public void visitForLoop(ForStatement node) {
        pushASTNode(node);
        try {
            super.visitForLoop(node);
        } finally {
            popASTNode();
        }
    }

    public void visitWhileLoop(WhileStatement node) {
        pushASTNode(node);
        try {
            super.visitWhileLoop(node);
        } finally {
            popASTNode();
        }
    }

    public void visitDoWhileLoop(DoWhileStatement node) {
        pushASTNode(node);
        try {
            super.visitDoWhileLoop(node);
        } finally {
            popASTNode();
        }
    }

    public void visitIfElse(IfStatement node) {
        pushASTNode(node);
        try {
            super.visitIfElse(node);
        } finally {
            popASTNode();
        }
    }

    public void visitExpressionStatement(ExpressionStatement node) {
        pushASTNode(node);
        try {
            super.visitExpressionStatement(node);
        } finally {
            popASTNode();
        }
    }

    public void visitReturnStatement(ReturnStatement node) {
        pushASTNode(node);
        try {
            super.visitReturnStatement(node);
        } finally {
            popASTNode();
        }
    }

    public void visitAssertStatement(AssertStatement node) {
        pushASTNode(node);
        try {
            super.visitAssertStatement(node);
        } finally {
            popASTNode();
        }
    }

    public void visitTryCatchFinally(TryCatchStatement node) {
        pushASTNode(node);
        try {
            super.visitTryCatchFinally(node);
        } finally {
            popASTNode();
        }
    }

    public void visitEmptyStatement(EmptyStatement node) {
        pushASTNode(node);
        try {
            super.visitEmptyStatement(node);
        } finally {
            popASTNode();
        }
    }

    public void visitSwitch(SwitchStatement node) {
        pushASTNode(node);
        try {
            super.visitSwitch(node);
        } finally {
            popASTNode();
        }
    }

    public void visitCaseStatement(CaseStatement node) {
        pushASTNode(node);
        try {
            super.visitCaseStatement(node);
        } finally {
            popASTNode();
        }
    }

    public void visitBreakStatement(BreakStatement node) {
        pushASTNode(node);
        try {
            super.visitBreakStatement(node);
        } finally {
            popASTNode();
        }
    }

    public void visitContinueStatement(ContinueStatement node) {
        pushASTNode(node);
        try {
            super.visitContinueStatement(node);
        } finally {
            popASTNode();
        }
    }

    public void visitSynchronizedStatement(SynchronizedStatement node) {
        pushASTNode(node);
        try {
            super.visitSynchronizedStatement(node);
        } finally {
            popASTNode();
        }
    }

    public void visitThrowStatement(ThrowStatement node) {
        pushASTNode(node);
        try {
            super.visitThrowStatement(node);
        } finally {
            popASTNode();
        }
    }

    public void visitMethodCallExpression(MethodCallExpression node) {
        pushASTNode(node);
        try {
            super.visitMethodCallExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitStaticMethodCallExpression(StaticMethodCallExpression node) {
        pushASTNode(node);
        try {
            super.visitStaticMethodCallExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitConstructorCallExpression(ConstructorCallExpression node) {
        pushASTNode(node);
        try {
            super.visitConstructorCallExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitBinaryExpression(BinaryExpression node) {
        pushASTNode(node);
        try {
            super.visitBinaryExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitTernaryExpression(TernaryExpression node) {
        pushASTNode(node);
        try {
            super.visitTernaryExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitShortTernaryExpression(ElvisOperatorExpression node) {
        pushASTNode(node);
        try {
            super.visitShortTernaryExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitPostfixExpression(PostfixExpression node) {
        pushASTNode(node);
        try {
            super.visitPostfixExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitPrefixExpression(PrefixExpression node) {
        pushASTNode(node);
        try {
            super.visitPrefixExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitBooleanExpression(BooleanExpression node) {
        pushASTNode(node);
        try {
            super.visitBooleanExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitNotExpression(NotExpression node) {
        pushASTNode(node);
        try {
            super.visitNotExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitClosureExpression(ClosureExpression node) {
        pushASTNode(node);
        try {
            super.visitClosureExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitTupleExpression(TupleExpression node) {
        pushASTNode(node);
        try {
            super.visitTupleExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitListExpression(ListExpression node) {
        pushASTNode(node);
        try {
            super.visitListExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitArrayExpression(ArrayExpression node) {
        pushASTNode(node);
        try {
            super.visitArrayExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitMapExpression(MapExpression node) {
        pushASTNode(node);
        try {
            super.visitMapExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitMapEntryExpression(MapEntryExpression node) {
        pushASTNode(node);
        try {
            super.visitMapEntryExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitRangeExpression(RangeExpression node) {
        pushASTNode(node);
        try {
            super.visitRangeExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitSpreadExpression(SpreadExpression node) {
        pushASTNode(node);
        try {
            super.visitSpreadExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitSpreadMapExpression(SpreadMapExpression node) {
        pushASTNode(node);
        try {
            super.visitSpreadMapExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitMethodPointerExpression(MethodPointerExpression node) {
        pushASTNode(node);
        try {
            super.visitMethodPointerExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitUnaryMinusExpression(UnaryMinusExpression node) {
        pushASTNode(node);
        try {
            super.visitUnaryMinusExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitUnaryPlusExpression(UnaryPlusExpression node) {
        pushASTNode(node);
        try {
            super.visitUnaryPlusExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitBitwiseNegationExpression(BitwiseNegationExpression node) {
        pushASTNode(node);
        try {
            super.visitBitwiseNegationExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitCastExpression(CastExpression node) {
        pushASTNode(node);
        try {
            super.visitCastExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitConstantExpression(ConstantExpression node) {
        pushASTNode(node);
        try {
            super.visitConstantExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitClassExpression(ClassExpression node) {
        pushASTNode(node);
        try {
            super.visitClassExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitVariableExpression(VariableExpression node) {
        pushASTNode(node);
        try {
            super.visitVariableExpression(node);
        } finally {
            popASTNode();
        }
    }

    // this calls visitBinaryExpression()
    // public void visitDeclarationExpression(DeclarationExpression node) {
    // pushASTNode(node);
    // try {
    // super.visitDeclarationExpression(node);
    // } finally {
    // popASTNode();
    // }
    // }

    public void visitPropertyExpression(PropertyExpression node) {
        pushASTNode(node);
        try {
            super.visitPropertyExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitAttributeExpression(AttributeExpression node) {
        pushASTNode(node);
        try {
            super.visitAttributeExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitFieldExpression(FieldExpression node) {
        pushASTNode(node);
        try {
            super.visitFieldExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitGStringExpression(GStringExpression node) {
        pushASTNode(node);
        try {
            super.visitGStringExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitCatchStatement(CatchStatement node) {
        pushASTNode(node);
        try {
            super.visitCatchStatement(node);
        } finally {
            popASTNode();
        }
    }

    // this calls visitTupleListExpression()
    // public void visitArgumentlistExpression(ArgumentListExpression node) {
    // pushASTNode(node);
    // try {
    // super.visitArgumentlistExpression(node);
    // } finally {
    // popASTNode();
    // }
    // }

    public void visitClosureListExpression(ClosureListExpression node) {
        pushASTNode(node);
        try {
            super.visitClosureListExpression(node);
        } finally {
            popASTNode();
        }
    }

    public void visitBytecodeExpression(BytecodeExpression node) {
        pushASTNode(node);
        try {
            super.visitBytecodeExpression(node);
        } finally {
            popASTNode();
        }
    }
}