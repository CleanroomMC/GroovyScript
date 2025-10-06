package com.cleanroommc.groovyscript.sandbox.transformer;

import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.spongepowered.asm.mixin.Mixin;

public class GroovyScriptMixinVerifier extends CompilationCustomizer {

    private static final ClassNode MIXIN_NODE = ClassHelper.makeCached(Mixin.class);

    public GroovyScriptMixinVerifier() {
        super(CompilePhase.CANONICALIZATION);
    }

    @Override
    public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
        for (AnnotationNode annotation : classNode.getAnnotations(MIXIN_NODE)) {
            Expression expr = annotation.getMember("value");
            String s = "";
        }
    }
}
