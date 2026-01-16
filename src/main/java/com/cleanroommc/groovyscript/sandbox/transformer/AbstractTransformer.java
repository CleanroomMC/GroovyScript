package com.cleanroommc.groovyscript.sandbox.transformer;

import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.control.SourceUnit;

import java.util.ArrayList;
import java.util.List;

public class AbstractTransformer extends ClassCodeExpressionTransformer {

    private final SourceUnit source;
    private final ClassNode classNode;

    public AbstractTransformer(SourceUnit source, ClassNode classNode) {
        this.source = source;
        this.classNode = classNode;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return source;
    }

    public ClassNode getClassNode() {
        return classNode;
    }

    protected static Expression makeCheckedCall(ClassNode classNode, String name, List<Expression> arguments) {
        return new StaticMethodCallExpression(classNode, name, new ArgumentListExpression(arguments));
    }

    protected static Expression makeCheckedCall(ClassNode classNode, String name, Expression arguments) {
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
}
