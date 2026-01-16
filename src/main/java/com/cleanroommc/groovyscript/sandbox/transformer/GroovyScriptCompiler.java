package com.cleanroommc.groovyscript.sandbox.transformer;

import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;

public class GroovyScriptCompiler extends AbstractCompileCustomizer {

    public GroovyScriptCompiler() {
        super(CompilePhase.CANONICALIZATION);
    }

    @Override
    protected AbstractTransformer createTransformer(SourceUnit source, ClassNode classNode) {
        return new GroovyScriptTransformer(source, classNode);
    }
}
