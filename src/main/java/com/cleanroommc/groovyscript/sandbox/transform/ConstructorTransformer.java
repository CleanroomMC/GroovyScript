package com.cleanroommc.groovyscript.sandbox.transform;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.sandbox.SandboxRunner;
import com.cleanroommc.groovyscript.sandbox.Visitor;
import com.cleanroommc.groovyscript.sandbox.interception.InterceptionManager;
import com.cleanroommc.groovyscript.sandbox.interception.SandboxSecurityException;
import groovy.lang.GroovySystem;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.control.SourceUnit;

import java.util.Arrays;

public class ConstructorTransformer {

    public static StaticMethodCallExpression wrapExpression(ConstructorCallExpression call, SourceUnit src) {
        ArgumentListExpression args = new ArgumentListExpression();

        args.addExpression(new ClassExpression(call.getType()));
        args.addExpression(new ConstantExpression("<init>"));
        args.addExpression(new ConstantExpression(src.getName()));
        args.addExpression(new ConstantExpression(call.getLineNumber()));
        args.addExpression(Visitor.toArgumentArray(call.getArguments(), src));

        return new StaticMethodCallExpression(new ClassNode(ConstructorTransformer.class), "invokeConstructor", args);
    }

    private static Object invokeConstructor(Class<?> target, String method, String sourceName, int lineNumber, Object[] args) throws SandboxSecurityException {
        GroovyScript.LOGGER.info("Try invoking constructor. Target {}, Method {}, Line {}, Args {}", target, method, lineNumber, Arrays.toString(args));
        if (!InterceptionManager.INSTANCE.isValid(sourceName, lineNumber, target.getName(), "<init>", false)) {
            SandboxRunner.logError("Prohibited constructor call on class '" + target.getName() + "'!", sourceName, lineNumber);
            //throw new SandboxSecurityException("Prohibited constructor call on class '" + target.getName() + "'!");
            return null;
        }
        return GroovySystem.getMetaClassRegistry().getMetaClass(target).invokeConstructor(args);
    }
}
