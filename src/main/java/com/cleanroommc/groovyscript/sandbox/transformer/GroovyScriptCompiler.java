package com.cleanroommc.groovyscript.sandbox.transformer;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;

public class GroovyScriptCompiler extends CompilationCustomizer {

    public static GroovyScriptCompiler transformer() {
        return new GroovyScriptCompiler(CompilePhase.CANONICALIZATION);
    }

    private GroovyScriptCompiler(CompilePhase phase) {
        super(phase);
    }

    @Override
    public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
        GroovyScriptTransformer visitor = new GroovyScriptTransformer(source, classNode);
        for (MethodNode m : classNode.getMethods()) {
            forbidIfFinalizer(m);
            visitor.visitMethod(m);
        }
        for (Statement s : classNode.getObjectInitializerStatements()) {
            s.visit(visitor);
        }
        for (FieldNode f : classNode.getFields()) {
            visitor.visitField(f);
        }
    }

    public void forbidIfFinalizer(MethodNode m) {
        if (m.getName().equals("finalize") && m.isVoidMethod() && !m.isPrivate() && !m.isStatic()) {
            boolean safe = false;
            for (Parameter p : m.getParameters()) {
                if (!p.hasInitialExpression()) {
                    safe = true;
                    break;
                }
            }
            if (!safe) {
                throw new SecurityException("Sandboxed code may not override Object.finalize()");
            }
        }
    }
}
