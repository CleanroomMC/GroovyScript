package com.cleanroommc.groovyscript.server;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.sandbox.GroovyScriptSandbox;
import com.cleanroommc.groovyscript.sandbox.security.GroovySecurityManager;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import net.minecraft.launchwrapper.Launch;
import net.prominic.groovyls.compiler.ILanguageServerContext;
import net.prominic.groovyls.compiler.documentation.DocumentationFactory;
import net.prominic.groovyls.compiler.documentation.GroovydocProvider;
import net.prominic.groovyls.util.FileContentsTracker;

public class GroovyScriptLanguageServerContext implements ILanguageServerContext {

    private final FileContentsTracker fileContentsTracker = new FileContentsTracker();

    private ScanResult scanResult = new ClassGraph()
            .enableClassInfo()
            .enableMethodInfo()
            .enableSystemJarsAndModules()
            .overrideClassLoaders(Launch.classLoader)
            .acceptPaths("*")
            .rejectClasses(GroovySecurityManager.INSTANCE.getBannedClasses().stream().map(Class::getName).toArray(String[]::new))
            .rejectPackages(GroovySecurityManager.INSTANCE.getBannedPackages().stream().toArray(String[]::new))
            .acceptClasses(GroovySecurityManager.INSTANCE.getWhiteListedClasses().stream().map(Class::getName).toArray(String[]::new))
            .scan();

    private final DocumentationFactory documentationFactory = new DocumentationFactory(new GroovyScriptDocumentationProvider(), new GroovydocProvider());

    public GroovyScriptSandbox getSandbox() {
        return GroovyScript.getSandbox();
    }

    public ScanResult getScanResult() {
        return this.scanResult;
    }

    @Override
    public FileContentsTracker getFileContentsTracker() {
        return fileContentsTracker;
    }

    @Override
    public DocumentationFactory getDocumentationFactory() {
        return documentationFactory;
    }
}
