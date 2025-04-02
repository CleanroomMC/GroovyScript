package com.cleanroommc.groovyscript.server;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.sandbox.transformer.GroovyScriptCompiler;
import com.cleanroommc.groovyscript.sandbox.transformer.GroovyScriptEarlyCompiler;
import groovy.lang.GroovyClassLoader;
import net.minecraft.launchwrapper.Launch;
import net.prominic.groovyls.compiler.control.GroovyLSCompilationUnit;
import net.prominic.groovyls.config.CompilationUnitFactoryBase;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class GroovyScriptCompilationUnitFactory extends CompilationUnitFactoryBase {

    private final File root;
    private final GroovyScriptLanguageServerContext languageServerContext;
    private final Map<URI, GroovyLSCompilationUnit> compilationUnitsByScript = new HashMap<>();

    public GroovyScriptCompilationUnitFactory(File root, GroovyScriptLanguageServerContext languageServerContext) {
        this.root = root;
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

        config.addCompilationCustomizers(new GroovyScriptCompiler());
        config.addCompilationCustomizers(new GroovyScriptEarlyCompiler());
        config.addCompilationCustomizers(languageServerContext.getSandbox().getImportCustomizer());

        return config;
    }

    @Override
    public GroovyLSCompilationUnit create(Path workspaceRoot, @Nullable URI context) {
        if (context == null || isInClassesContext(context)) {
            context = null; // actions on classes are going into classes only unit
        }

        var unit = compilationUnitsByScript.computeIfAbsent(context, uri -> new GroovyLSCompilationUnit(getConfiguration(), null, GroovyScript.getSandbox().getEngine().getClassLoader(), languageServerContext));

        var changedUris = languageServerContext.getFileContentsTracker().getChangedURIs();

        removeSources(unit, changedUris);

        // add open classes
        languageServerContext.getFileContentsTracker()
                .getOpenURIs()
                .stream()
                .filter(uri -> {
                    Path openPath = Paths.get(uri);
                    return openPath.normalize().startsWith(workspaceRoot.normalize());
                })
                .filter(this::isInClassesContext)
                .forEach(uri -> {
                    addOpenFileToCompilationUnit(uri, languageServerContext.getFileContentsTracker().getContents(uri), unit);
                });

        // add all other classes too
        /*getAllClasses()
                .filter(path -> !languageServerContext.getFileContentsTracker().isOpen(path.toUri()))
                .forEach(path -> {
                    addOpenFileToCompilationUnit(path.toUri(), languageServerContext.getFileContentsTracker().getContents(path.toUri()), unit);
                });*/

        if (context != null) {
            var contents = languageServerContext.getFileContentsTracker().getContents(context);

            // we're in script context so only classes and the script itself
            addOpenFileToCompilationUnit(context, contents, unit);
        }

        return unit;
    }

    protected boolean isInClassesContext(URI uri) {
        return false;
        //var file = Paths.get(uri).getParent();

        //return getAllClasses().anyMatch(file::startsWith);
    }

    /*protected Stream<Path> getAllClasses() {
        return LoadStage.getLoadStages()
                .stream()
                .map(LoadStage::getName)
                .flatMap(loader -> GroovyScript.getRunConfig().getClassFiles(this.root, loader).stream())
                .map(File::toPath)
                .map(path -> GroovyScript.getScriptFile().toPath().resolve(path));
    }*/

    protected void removeSources(GroovyLSCompilationUnit unit, Set<URI> urisToRemove) {
        List<SourceUnit> sourcesToRemove = new ArrayList<>();
        unit.iterator().forEachRemaining(sourceUnit -> {
            URI uri = sourceUnit.getSource().getURI();
            if (urisToRemove.contains(uri)) {
                sourcesToRemove.add(sourceUnit);
            }
        });

        // if a URI has changed, we remove it from the compilation unit so
        // that a new version can be built from the updated source file
        unit.removeSources(sourcesToRemove);
    }
}
