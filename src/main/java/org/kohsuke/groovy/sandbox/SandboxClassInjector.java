package org.kohsuke.groovy.sandbox;

import com.cleanroommc.groovyscript.sandbox.AliasGroovyManager;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;

import java.util.Iterator;
import java.util.Map;

public class SandboxClassInjector extends CompilationCustomizer {

    public SandboxClassInjector() {
        super(CompilePhase.PARSING);
    }

    @Override
    public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
    }

    @Override
    public void doPhaseOperation(CompilationUnit unit) throws CompilationFailedException {
        for (Iterator<Map.Entry<String, Class<?>>> it = AliasGroovyManager.getClassAliasIterator(); it.hasNext(); ) {
            Map.Entry<String, Class<?>> entry = it.next();
            unit.getAST().addClass(new AliasClassNode(entry.getValue(), entry.getKey()));
        }
    }
}
