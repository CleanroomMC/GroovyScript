package org.kohsuke.groovy.sandbox;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.*;

/**
 * Keeps track of in-scope variables.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class ScopeTrackingClassCodeExpressionTransformer extends ClassCodeExpressionTransformer {
    /**
     * As we visit expressions, track variable scopes.
     * This is used to distinguish local variables from property access. See issue #11.
     */
    StackVariableSet varScope;

    public boolean isLocalVariable(String name) {
        return varScope.has(name);
    }

    @Override
    public void visitMethod(MethodNode node) {
        varScope = null;
        try (StackVariableSet scope = new StackVariableSet(this)) {
            for (Parameter p : node.getParameters()) {
                declareVariable(p);
            }
            super.visitMethod(node);
        }
    }

    void withMethod(MethodNode node, Runnable r) {
        varScope = null;
        try (StackVariableSet scope = new StackVariableSet(this)) {
            for (Parameter p : node.getParameters()) {
                declareVariable(p);
            }
            r.run();
        }
    }

    @Override
    public void visitField(FieldNode node) {
        try (StackVariableSet scope = new StackVariableSet(this)) {
            super.visitField(node);
        }
    }

    @Override
    public void visitBlockStatement(BlockStatement block) {
        try (StackVariableSet scope = new StackVariableSet(this)) {
            super.visitBlockStatement(block);
        }
    }

    @Override
    public void visitDoWhileLoop(DoWhileStatement loop) {
        // Do-while loops are not actually supported by Groovy 2.x.
        try (StackVariableSet scope = new StackVariableSet(this)) {
            loop.getLoopBlock().visit(this);
        }
        try (StackVariableSet scope = new StackVariableSet(this)) {
            loop.setBooleanExpression((BooleanExpression) transform(loop.getBooleanExpression()));
        }
    }

    @Override
    public void visitForLoop(ForStatement forLoop) {
        try (StackVariableSet scope = new StackVariableSet(this)) {
            /*
                Groovy appears to always treat the left-hand side of forLoop as a declaration.
                i.e., the following code is error

                def h() {
                    def x =0;
                    def i = 0;
                    for (i in 0..9 ) {
                        x+= i;
                    }
                    println x;
                }

                script1414457812466.groovy: 18: The current scope already contains a variable of the name i
                 @ line 18, column 5.
                       for (i in 0..9 ) {
                       ^

                1 error

                Also see issue 17.
             */
            declareVariable(forLoop.getVariable());
            super.visitForLoop(forLoop);
        }
    }

    @Override
    public void visitIfElse(IfStatement ifElse) {
        try (StackVariableSet scope = new StackVariableSet(this)) {
            ifElse.setBooleanExpression((BooleanExpression) transform(ifElse.getBooleanExpression()));
        }
        try (StackVariableSet scope = new StackVariableSet(this)) {
            ifElse.getIfBlock().visit(this);
        }
        try (StackVariableSet scope = new StackVariableSet(this)) {
            ifElse.getElseBlock().visit(this);
        }
    }

    @Override
    public void visitSwitch(SwitchStatement statement) {
        try (StackVariableSet scope = new StackVariableSet(this)) {
            super.visitSwitch(statement);
        }
    }

    @Override
    public void visitSynchronizedStatement(SynchronizedStatement sync) {
        try (StackVariableSet scope = new StackVariableSet(this)) {
            super.visitSynchronizedStatement(sync);
        }
    }

    @Override
    public void visitTryCatchFinally(TryCatchStatement statement) {
        try (StackVariableSet scope = new StackVariableSet(this)) {
            super.visitTryCatchFinally(statement);
        }
    }

    @Override
    public void visitCatchStatement(CatchStatement statement) {
        try (StackVariableSet scope = new StackVariableSet(this)) {
            declareVariable(statement.getVariable());
            super.visitCatchStatement(statement);
        }
    }

    @Override
    public void visitWhileLoop(WhileStatement loop) {
        try (StackVariableSet scope = new StackVariableSet(this)) {
            super.visitWhileLoop(loop);
        }
    }

    @Override
    public void visitClosureExpression(ClosureExpression expression) {
        try (StackVariableSet scope = new StackVariableSet(this)) {
            super.visitClosureExpression(expression);
        }
    }

    /**
     * @see org.codehaus.groovy.classgen.asm.BinaryExpressionHelper#evaluateEqual(org.codehaus.groovy.ast.expr.BinaryExpression, boolean)
     */
    void handleDeclarations(DeclarationExpression exp) {
        Expression leftExpression = exp.getLeftExpression();
        if (leftExpression instanceof VariableExpression) {
            declareVariable((VariableExpression) leftExpression);
        } else if (leftExpression instanceof TupleExpression) {
            TupleExpression te = (TupleExpression) leftExpression;
            for (Expression e : te.getExpressions()) {
                declareVariable((VariableExpression) e);
            }
        }
    }

    protected void declareVariable(Variable exp) {
        varScope.declare(exp.getName());
    }
}
