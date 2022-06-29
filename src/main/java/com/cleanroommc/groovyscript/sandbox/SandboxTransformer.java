package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;

public class SandboxTransformer extends CompilationCustomizer {

    public SandboxTransformer() {
        super(CompilePhase.CANONICALIZATION);
    }

    @Override
    public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {

        Visitor visitor = new Visitor(source);

        for (MethodNode methodNode : classNode.getMethods()) {
            GroovyScript.LOGGER.info("Visiting method {}", methodNode);
            visitor.visitMethod(methodNode);
        }

        for (Statement statement : classNode.getObjectInitializerStatements()) {
            GroovyScript.LOGGER.info("Visiting statement {}", statement);
            statement.visit(visitor);
        }

        for (FieldNode fieldNode : classNode.getFields()) {
            GroovyScript.LOGGER.info("Visiting field {}", fieldNode);
            visitor.visitField(fieldNode);
        }
    }
}
