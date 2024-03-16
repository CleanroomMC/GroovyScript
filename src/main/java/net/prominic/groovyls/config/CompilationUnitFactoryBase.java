package net.prominic.groovyls.config;

import groovy.lang.GroovyClassLoader;
import net.prominic.groovyls.compiler.control.GroovyLSCompilationUnit;
import net.prominic.groovyls.compiler.control.io.StringReaderSourceWithURI;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CompilationUnitFactoryBase implements ICompilationUnitFactory {

    protected CompilerConfiguration config;
    protected GroovyClassLoader classLoader;
    protected List<String> additionalClasspathList;

    public List<String> getAdditionalClasspathList() {
        return additionalClasspathList;
    }

    public void setAdditionalClasspathList(List<String> additionalClasspathList) {
        this.additionalClasspathList = additionalClasspathList;
        invalidateCompilationUnit();
    }

    public void invalidateCompilationUnit() {
        config = null;
        classLoader = null;
    }

    protected GroovyClassLoader getClassLoader() {
        return new GroovyClassLoader(ClassLoader.getSystemClassLoader().getParent(), config, true);
    }

    protected CompilerConfiguration getConfiguration() {
        CompilerConfiguration config = new CompilerConfiguration();

        Map<String, Boolean> optimizationOptions = new HashMap<>();
        optimizationOptions.put(CompilerConfiguration.GROOVYDOC, true);
        config.setOptimizationOptions(optimizationOptions);

        List<String> classpathList = new ArrayList<>();
        getClasspathList(classpathList);
        config.setClasspathList(classpathList);

        return config;
    }

    protected void getClasspathList(List<String> result) {
        if (additionalClasspathList == null) {
            return;
        }

        for (String entry : additionalClasspathList) {
            boolean mustBeDirectory = false;
            if (entry.endsWith("*")) {
                entry = entry.substring(0, entry.length() - 1);
                mustBeDirectory = true;
            }

            File file = new File(entry);
            if (!file.exists()) {
                continue;
            }
            if (file.isDirectory()) {
                for (File child : file.listFiles()) {
                    if (!child.getName().endsWith(".jar") || !child.isFile()) {
                        continue;
                    }
                    result.add(child.getPath());
                }
            } else if (!mustBeDirectory && file.isFile()) {
                if (file.getName().endsWith(".jar")) {
                    result.add(entry);
                }
            }
        }
    }

    protected void addOpenFileToCompilationUnit(URI uri, String contents, GroovyLSCompilationUnit compilationUnit) {
        Path filePath = Paths.get(uri);
        SourceUnit sourceUnit = new SourceUnit(filePath.toString(),
                                               new StringReaderSourceWithURI(contents, uri, compilationUnit.getConfiguration()),
                                               compilationUnit.getConfiguration(), compilationUnit.getClassLoader(),
                                               compilationUnit.getErrorCollector());
        compilationUnit.addSource(sourceUnit);
    }
}
