package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.sandbox.transform.ConstructorTransformer;
import com.cleanroommc.groovyscript.sandbox.transform.MethodCallTransformer;
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.control.SourceUnit;

import java.util.ArrayList;
import java.util.List;

public class Visitor extends ClassCodeExpressionTransformer {

    private final SourceUnit src;

    public Visitor(SourceUnit src) {
        this.src = src;
    }

    @Override
    public Expression transform(Expression expr) {
        if (expr instanceof ConstructorCallExpression) {
            return ConstructorTransformer.wrapExpression((ConstructorCallExpression) expr, src);
        }
        if (expr instanceof MethodCallExpression) {
            return MethodCallTransformer.wrapExpression((MethodCallExpression) expr, src);
        }
        return super.transform(expr);
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return src;
    }

    public static Expression toArgumentArray(Expression arguments, SourceUnit src) {

        List<Expression> argumentList;

        if (arguments instanceof NamedArgumentListExpression) {

            argumentList = new ArrayList<Expression>();
            argumentList.add(arguments);
        } else {
            TupleExpression tuple = (TupleExpression) arguments;
            argumentList = tuple.getExpressions();
        }

        // Disallow SpreadExpressions
        for (Expression exp : argumentList) {
            if (exp instanceof SpreadExpression) {
                GroovyScript.LOGGER.error(src.getName() + ": SpreadExpression encountered and disallowed!");
                return null;
            }
        }

        return new ArrayExpression(ClassHelper.OBJECT_TYPE, argumentList);
    }
}
