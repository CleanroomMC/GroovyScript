package com.cleanroommc.groovyscript.sandbox.transform;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.sandbox.SandboxRunner;
import com.cleanroommc.groovyscript.sandbox.Visitor;
import com.cleanroommc.groovyscript.sandbox.interception.InterceptionManager;
import com.cleanroommc.groovyscript.sandbox.interception.SandboxSecurityException;
import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.GroovySystem;
import groovy.lang.MetaMethod;
import groovyjarjarasm.asm.MethodVisitor;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.classgen.BytecodeExpression;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.HandleMetaClass;
import org.codehaus.groovy.runtime.MethodClosure;

import java.util.Arrays;

public class MethodCallTransformer {

    public static StaticMethodCallExpression wrapExpression(MethodCallExpression call, SourceUnit src) {
        ArgumentListExpression args = new ArgumentListExpression();

        if (call.getObjectExpression() instanceof VariableExpression && call.getObjectExpression().getText().equals("this")) {
            args.addExpression(new BytecodeExpression() {
                @Override
                public void visit(MethodVisitor methodVisitor) {
                    methodVisitor.visitVarInsn(25, 0);
                }
            });
        } else {
            args.addExpression(call.getObjectExpression());
        }
        args.addExpression(call.getMethod());
        args.addExpression(new ConstantExpression(src.getName()));
        args.addExpression(new ConstantExpression(call.getLineNumber()));
        args.addExpression(Visitor.toArgumentArray(call.getArguments(), src));

        return new StaticMethodCallExpression(new ClassNode(MethodCallTransformer.class), "invokeMethod", args);
    }

    private static Object invokeMethod(Object target, String method, String sourceName, int lineNumber, Object[] args) throws SandboxSecurityException {
        GroovyScript.LOGGER.info("Try invoking method. Target {}, Method {}, Line {}, Args {}", target, method, lineNumber, Arrays.toString(args));

        while (target instanceof Closure) {
            target = (target instanceof MethodClosure) ? ((MethodClosure) target).getMethod() : ((Closure<?>) target).getDelegate();
        }

        Class<?> clazz = target.getClass();
        String className = clazz.getName();
        if (target instanceof Class) {
            className = ((Class<?>) target).getName();

            if (!InterceptionManager.INSTANCE.isValid(sourceName, lineNumber, className, method, false)) {
                SandboxRunner.logError("Prohibited method call on class '" + className + "'!", sourceName, lineNumber);
                return null;
            }

            return GroovySystem.getMetaClassRegistry().getMetaClass((Class<?>) target).invokeStaticMethod(target, method, args);
        }

        if (target instanceof GroovyObjectSupport) {
            if (!InterceptionManager.INSTANCE.isValid(sourceName, lineNumber, className, method, true)) {
                SandboxRunner.logError("Prohibited method call on groovy object class '" + className + "'!", sourceName, lineNumber);
                return null;
            }

            GroovyObjectSupport gos = (GroovyObjectSupport) target;
            return gos.invokeMethod(method, args);
        }

        if (target instanceof HandleMetaClass) {
            // Meta Class support

            HandleMetaClass xmc = (HandleMetaClass) target;
            Object[] nargs = new Object[args.length - 1];
            System.arraycopy(args, 1, nargs, 0, args.length - 1);
            MetaMethod mm = xmc.getMetaMethod((String) args[0], nargs);
            String cn = xmc.getTheClass().getName();

            if (!InterceptionManager.INSTANCE.isValid(sourceName, lineNumber, cn, mm.getName(), false)) {
                SandboxRunner.logError("Prohibited method call on meta class '" + className + "'!", sourceName, lineNumber);
                return null;
            }

            return mm.invoke(target, nargs);
        }

        if (!InterceptionManager.INSTANCE.isValid(sourceName, lineNumber, className, method, false)) {
            SandboxRunner.logError("Prohibited constructor call on class '" + className + "'!", sourceName, lineNumber);
            return null;
        }

        return GroovySystem.getMetaClassRegistry().getMetaClass(target.getClass()).invokeMethod(target, method, args);
    }
}
