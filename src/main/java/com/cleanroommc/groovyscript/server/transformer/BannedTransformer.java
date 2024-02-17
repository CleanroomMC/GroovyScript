package com.cleanroommc.groovyscript.server.transformer;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;

/*
    TODO:
     - generate errors for methods in GroovySecurityManager.INSTANCE.getBannedMethods()
 */
public class BannedTransformer extends CompilationCustomizer {

    public BannedTransformer(CompilePhase phase) {
        super(phase);
    }

    @Override
    public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {

    }
}
