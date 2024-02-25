package com.cleanroommc.groovyscript.server;

import com.cleanroommc.groovyscript.sandbox.transformer.GroovyScriptCompiler;
import groovy.lang.GroovyClassLoader;
import net.minecraft.launchwrapper.Launch;
import net.prominic.groovyls.config.CompilationUnitFactory;
import org.codehaus.groovy.control.CompilerConfiguration;

public class GroovyScriptCompilationUnitFactory extends CompilationUnitFactory {

    private final GroovyScriptLanguageServerContext languageServerContext;

    public GroovyScriptCompilationUnitFactory(GroovyScriptLanguageServerContext languageServerContext) {
        this.languageServerContext = languageServerContext;
    }

    @Override
    protected GroovyClassLoader getClassLoader() {
        return new GroovyClassLoader(Launch.classLoader, config, true);
    }

    @Override
    protected CompilerConfiguration getConfiguration() {
        var config = super.getConfiguration();

        config.setSourceEncoding("UTF-8");

        config.addCompilationCustomizers(GroovyScriptCompiler.transformer());
        config.addCompilationCustomizers(languageServerContext.getSandbox().getImportCustomizer());

        return config;
    }
}
