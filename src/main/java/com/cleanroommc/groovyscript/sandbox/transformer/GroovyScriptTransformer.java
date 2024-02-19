package com.cleanroommc.groovyscript.sandbox.transformer;

import com.cleanroommc.groovyscript.gameobjects.GameObjectHandlerManager;
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.control.SourceUnit;

import java.util.ArrayList;
import java.util.List;

public class GroovyScriptTransformer extends ClassCodeExpressionTransformer {

    private static final ClassNode bracketHandlerClass = ClassHelper.makeCached(GameObjectHandlerManager.class);
    private final SourceUnit source;
    private final ClassNode classNode;

    public GroovyScriptTransformer(SourceUnit source, ClassNode classNode) {
        this.source = source;
        this.classNode = classNode;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return source;
    }

    private static Expression makeCheckedCall(ClassNode classNode, String name, Expression... arguments) {
        return new StaticMethodCallExpression(classNode, name,
                                              new ArgumentListExpression(arguments));
    }

    @Override
    public Expression transform(Expression expr) {
        if (expr == null) return null;
        Expression expr2 = transformInternal(expr);
        if (expr2 != expr) {
            expr2.setSourcePosition(expr);
            return expr2;
        }
        return super.transform(expr);
    }

    private Expression transformInternal(Expression expr) {
        if (expr instanceof ClosureExpression) {
            return transformClosure((ClosureExpression) expr);
        }
        if (expr instanceof MethodCallExpression) {
            return checkValid((MethodCallExpression) expr);
        }
        return expr;
    }

    private Expression transformClosure(ClosureExpression closure) {
        // ClosureExpression.transformExpression doesn't visit the code inside
        Parameter[] parameters = closure.getParameters();
        if (parameters != null) {
            // Explicitly defined parameters, i.e., ".findAll { i -> i == 'bar' }"
            for (Parameter p : parameters) {
                if (p.hasInitialExpression()) {
                    Expression init = p.getInitialExpression();
                    p.setInitialExpression(transform(init));
                }
            }
        }
        closure.getCode().visit(this);
        return closure;
    }

    private Expression checkValid(MethodCallExpression expression) {
        int argCount = 0;
        if (expression.getArguments() instanceof TupleExpression) {
            argCount = ((TupleExpression) expression.getArguments()).getExpressions().size();
        }
        if (expression.isImplicitThis() && argCount > 0) {
            String name = expression.getMethodAsString();
            if (GameObjectHandlerManager.hasGameObjectHandler(name)) {
                List<Expression> args;
                if (expression.getArguments() instanceof TupleExpression) {
                    args = ((TupleExpression) expression.getArguments()).getExpressions();
                } else {
                    args = new ArrayList<>();
                }
                args.add(0, new ConstantExpression(name));
                return makeCheckedCall(bracketHandlerClass, "getGameObject", args.toArray(new Expression[0]));
            }
        }
        return expression;
    }
}
