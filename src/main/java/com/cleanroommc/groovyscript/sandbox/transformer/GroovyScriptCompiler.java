package com.cleanroommc.groovyscript.sandbox.transformer;

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;

public class GroovyScriptCompiler extends CompilationCustomizer {

    private static final String SIDE_ONLY_CLASS = "net.minecraftforge.fml.relauncher.SideOnly";
    private static final String SIDE_CLASS = "net.minecraftforge.fml.relauncher.Side";

    public static GroovyScriptCompiler transformer() {
        return new GroovyScriptCompiler(CompilePhase.CANONICALIZATION);
    }

    private GroovyScriptCompiler(CompilePhase phase) {
        super(phase);
    }

    @Override
    public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
        GroovyScriptTransformer visitor = new GroovyScriptTransformer(source, classNode);
        classNode.getMethods().removeIf(m -> {
            if (isBannedFromSide(m)) return true;
            forbidIfFinalizer(m);
            visitor.visitMethod(m);
            return false;
        });
        for (Statement s : classNode.getObjectInitializerStatements()) {
            s.visit(visitor);
        }
        classNode.getFields().removeIf(f -> {
            if (isBannedFromSide(f)) return true;
            visitor.visitField(f);
            return false;
        });
    }

    private static boolean isBannedFromSide(AnnotatedNode node) {
        for (AnnotationNode annotatedNode : node.getAnnotations()) {
            if (annotatedNode.getClassNode().getName().equals(SIDE_ONLY_CLASS)) {
                Expression expr = annotatedNode.getMember("value");
                if (expr instanceof PropertyExpression) {
                    PropertyExpression prop = (PropertyExpression) expr;
                    if (prop.getObjectExpression() instanceof ClassExpression &&
                        prop.getObjectExpression().getType().getName().equals(SIDE_CLASS)) {
                        String elementSide = prop.getPropertyAsString();
                        return elementSide != null && !elementSide.equals(AsmDecompileHelper.SIDE);
                    }
                }
            }
        }
        return false;
    }

    public void forbidIfFinalizer(MethodNode m) {
        if ("finalize".equals(m.getName()) && m.isVoidMethod() && !m.isPrivate() && !m.isStatic()) {
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
