package com.cleanroommc.groovyscript.sandbox.transformer;

import com.cleanroommc.groovyscript.gameobjects.GameObjectHandlerManager;
import com.cleanroommc.groovyscript.helper.GroovyFile;
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.syntax.SyntaxException;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GroovyScriptTransformer extends ClassCodeExpressionTransformer {

    private static final ClassNode bracketHandlerClass = ClassHelper.makeCached(GameObjectHandlerManager.class);
    private static final ClassNode groovyFile = ClassHelper.makeCached(GroovyFile.class);
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

    private static Expression makeCheckedCall(ClassNode classNode, String name, List<Expression> arguments) {
        return new StaticMethodCallExpression(classNode, name, new ArgumentListExpression(arguments));
    }

    private static Expression makeCheckedCall(ClassNode classNode, String name, Expression arguments) {
        return makeCheckedCall(classNode, name, getArguments(arguments));
    }

    private static List<Expression> getArguments(Expression expr) {
        List<Expression> args;
        if (expr instanceof TupleExpression te) {
            args = new ArrayList<>(te.getExpressions());
        } else {
            args = new ArrayList<>();
            args.add(expr);
        }
        return args;
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
        if (expr instanceof ConstructorCallExpression cce) {
            if (cce.getType().getName().equals(File.class.getName())) {
                // redirect to file wrapper
                cce.setType(groovyFile);
            } else if (cce.getType().getName().equals(PrintWriter.class.getName())) {
                // we need to whitelist PrintWriter for print methods, but creating PrintWriter is still disallowed
                source.addError(new SyntaxException("Not allowed to create PrintWriter!", expr));
            }
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
                List<Expression> args = getArguments(expression.getArguments());
                args.add(0, new ConstantExpression(name));
                return makeCheckedCall(bracketHandlerClass, "getGameObject", args);
            }
        }
        return expression;
    }
}
