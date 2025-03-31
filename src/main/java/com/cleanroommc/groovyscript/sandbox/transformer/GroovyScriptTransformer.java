package com.cleanroommc.groovyscript.sandbox.transformer;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.GroovyFile;
import com.cleanroommc.groovyscript.mapper.AbstractObjectMapper;
import com.cleanroommc.groovyscript.mapper.ObjectMapperManager;
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
import java.util.stream.Collectors;

public class GroovyScriptTransformer extends ClassCodeExpressionTransformer {

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
        if (expr instanceof ClosureExpression ce) {
            return transformClosure(ce);
        }
        if (expr instanceof MethodCallExpression mce) {
            return transformMethodCall(mce);
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

    private Expression transformMethodCall(MethodCallExpression mce) {
        if (mce.isImplicitThis()) {
            List<AbstractObjectMapper<?>> conflicts = ObjectMapperManager.getConflicts(mce.getMethodAsString());
            if (conflicts != null) {
                List<String> suggestions = conflicts.stream()
                        .map(goh -> goh.getMod() == null ? goh.getName() : "mods." + goh.getMod().getModId() + "." + goh.getName())
                        .collect(Collectors.toList());
                String msg = GroovyLog.format("Can't infer ObjectMapper from name {}, since one is added by {} mods. " + "Please choose one of the following: {}", mce.getMethodAsString(), conflicts.size(), suggestions);
                source.addError(new SyntaxException(msg, mce));
            }
        }
        return mce;
    }
}
