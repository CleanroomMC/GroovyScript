package com.cleanroommc.groovyscript.sandbox.transformer;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.brackets.BracketHandlerManager;
import com.cleanroommc.groovyscript.sandbox.interception.InterceptionManager;
import groovy.lang.MetaMethod;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.reflection.CachedMethod;
import org.codehaus.groovy.runtime.metaclass.ReflectionMetaMethod;
import org.kohsuke.groovy.sandbox.ScopeTrackingClassCodeExpressionTransformer;
import org.kohsuke.groovy.sandbox.StackVariableSet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class GroovyScriptTransformer extends ScopeTrackingClassCodeExpressionTransformer {

    private static final ClassNode bracketHandlerClass = ClassHelper.makeCached(BracketHandlerManager.class);
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

    private static Expression makeSecurityError(String msg) {
        return makeCheckedCall(bracketHandlerClass, "throwSecurityError", new ConstantExpression(msg));
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
        GroovyScript.LOGGER.info("Transfroming:  {}", expr);
        try {
            if (expr.getType() != null && !InterceptionManager.INSTANCE.isValid(expr.getType().getTypeClass())) {
                return makeSecurityError("Prohibited class access on " + expr.getType().getName());
            }
        } catch (GroovyBugError e) {
            GroovyLog.get().errorMC(e.getBugText());
        }

        if (expr instanceof ClosureExpression) {
            return transformClosure((ClosureExpression) expr);
        }
        if (expr instanceof MethodCallExpression) {
            return checkValid((MethodCallExpression) expr);
        }
        if (expr instanceof StaticMethodCallExpression) {
            return checkValid((StaticMethodCallExpression) expr);
        }
        return expr;
    }

    private Expression transformClosure(ClosureExpression closure) {
        // ClosureExpression.transformExpression doesn't visit the code inside
        try (StackVariableSet scope = new StackVariableSet(this)) {
            Parameter[] parameters = closure.getParameters();
            if (parameters != null) {
                // Explicitly defined parameters, i.e., ".findAll { i -> i == 'bar' }"
                if (parameters.length > 0) {
                    for (Parameter p : parameters) {
                        if (p.hasInitialExpression()) {
                            Expression init = p.getInitialExpression();
                            p.setInitialExpression(transform(init));
                        }
                    }
                    for (Parameter p : parameters) {
                        declareVariable(p);
                    }
                } else {
                    // Implicit parameter - i.e., ".findAll { it == 'bar' }"
                    declareVariable(new Parameter(ClassHelper.DYNAMIC_TYPE, "it"));
                }
            }
            closure.getCode().visit(this);
        }
        return closure;
    }

    private Expression checkValid(MethodCallExpression expression) {
        MethodNode method = expression.getMethodTarget();
        GroovyScript.LOGGER.info("  - method call: {}", expression.getMethodAsString());
        int argCount = 0;
        if (expression.getArguments() instanceof TupleExpression) {
            argCount = ((TupleExpression) expression.getArguments()).getExpressions().size();
        }
        if (expression.isImplicitThis() && argCount > 0) {
            GroovyScript.LOGGER.info("  - maybe bracket handler");
            String name = expression.getMethodAsString();
            if (BracketHandlerManager.getBracketHandler(name) != null) {
                List<Expression> args;
                if (expression.getArguments() instanceof TupleExpression) {
                    args = ((TupleExpression) expression.getArguments()).getExpressions();
                } else {
                    args = new ArrayList<>();
                }
                args.add(0, new ConstantExpression(name));
                return makeCheckedCall(bracketHandlerClass, "handleBracket", args.toArray(new Expression[0]));
            }
        }
        if (method == null) {
            Class<?> type = expression.getObjectExpression().getType().getTypeClass();
            method = findMethod(type, expression.getMethodAsString(), argCount);
            if (method == null) {
                return expression;
            }
            expression.setMethodTarget(method);
        }
        for (AnnotationNode annotation : method.getAnnotations()) {
            Class<?> clazz = annotation.getClassNode().getTypeClass();
            if (clazz == GroovyBlacklist.class) {
                GroovyScript.LOGGER.info("  - blacklisted");
                return makeSecurityError("Prohibited method call '" + method.getName() + "' on class '" + clazz.getName() + "'!");
            }
        }
        return expression;
    }

    private Expression checkValid(StaticMethodCallExpression expression) {
        GroovyScript.LOGGER.info("  - static method call: {}", expression.getMethod());
        if (expression.getMetaMethod() == null) {
            return expression;
        }
       /* AnnotatedElement method = findMethod(expression.getMetaMethod());
        if (method == null) {
            GroovyScript.LOGGER.info("  - no method found for {}", expression.getMetaMethod().getClass());
            return expression;
        }
        if (method.isAnnotationPresent(GroovyBlacklist.class)) {
            return makeSecurityError("Prohibited method call '" + expression.getMethod() + "' on class '" + expression.getOwnerType().getTypeClass().getName() + "'!");
        }*/
        return expression;
    }

    private MethodNode findMethod(Class<?> receiver, String methodName, int argCount) {
        ClassNode classNode1 = ClassHelper.makeCached(receiver);
        List<MethodNode> methods = classNode1.getMethods(methodName);
        methods.removeIf(methodNode -> methodNode.getParameters().length != argCount);
        if (methods.size() == 1) {
            return methods.get(0);
        }
        return null;
    }

    private Method findMethod(MetaMethod method) {
        while (method instanceof ReflectionMetaMethod) {
            method = ((ReflectionMetaMethod) method).getCachedMethod();
        }
        if (method instanceof CachedMethod) {
            return ((CachedMethod) method).getCachedMethod();
        }
        return null;
    }
}
