package com.cleanroommc.groovyscript.sandbox.transformer;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.core.mixin.groovy.ModuleNodeAccessor;
import com.cleanroommc.groovyscript.sandbox.FileUtil;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;

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
            // inject correct package declaration into script
            String packageName = rel.substring(0, i).replace(File.separatorChar, '.') + '.';
            if (module.getPackage() != null && !module.getPackage().getName().equals(packageName)) {
                GroovyLog.get().error("Expected package {} but got {} in script {}", packageName, module.getPackage().getName(), rel);
            }
            module.setPackageName(packageName);
        }
        BlockStatement scriptStatement = (BlockStatement) module.getClasses().get(0).getMethods("run").get(0).getCode();
        // transform 'import mods.[mod].[registry]' statements into 'def [registry] = mods.[mod].[registry]' expressions
        ((ModuleNodeAccessor) module).getModifiableImports().removeIf(imp -> {
            ClassNode type = imp.getType();
            if (type.getName().startsWith("mods.")) {
                String[] parts = type.getName().split("\\.");
                if (!ModSupport.INSTANCE.hasCompatFor(parts[1]) || parts.length > 3) return false;
                GroovyContainer<?> mpc = ModSupport.INSTANCE.getContainer(parts[1]);
                if (!mpc.isLoaded()) return true; // mod not loaded -> remove import
                Expression prop = new PropertyExpression(new VariableExpression("mods", ClassHelper.makeCached(ModSupport.class)), parts[1]);
                if (parts.length > 2) {
                    prop = new PropertyExpression(prop, parts[2]);
                }
                Expression expr = new DeclarationExpression(new VariableExpression(imp.getAlias()),
                                                            Token.newSymbol(Types.ASSIGN, imp.getLineNumber(), 5 + parts[1].length()),
                                                            prop);
                scriptStatement.getStatements().add(0, new ExpressionStatement(expr));
                return true;
            }
            return false;
        });
    }
}
