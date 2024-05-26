package com.cleanroommc.groovyscript.sandbox.transformer;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.sandbox.FileUtil;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;

import java.io.File;

public class GroovyScriptEarlyCompiler extends CompilationCustomizer {

    public GroovyScriptEarlyCompiler() {
        super(CompilePhase.CONVERSION);
    }

    @Override
    public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
        String root = GroovyScript.getScriptPath();
        String script = source.getName();
        ModuleNode module = classNode.getModule();
        String rel = FileUtil.relativize(root, script);
        int i = rel.lastIndexOf(File.separatorChar);
        if (i >= 0) {
            String packageName = rel.substring(0, i).replace(File.separatorChar, '.') + '.';
            if (module.getPackage() != null && !module.getPackage().getName().equals(packageName)) {
                GroovyLog.get().error("Expected package {} but got {} in script {}", packageName, module.getPackage().getName(), rel);
            }
            module.setPackageName(packageName);
        }
    }
}
