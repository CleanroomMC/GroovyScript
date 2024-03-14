package net.prominic.groovyls.compiler;

import com.cleanroommc.groovyscript.sandbox.GroovySandbox;
import io.github.classgraph.ScanResult;
import net.prominic.groovyls.compiler.documentation.DocumentationFactory;
import net.prominic.groovyls.util.FileContentsTracker;

public interface ILanguageServerContext {

    GroovySandbox getSandbox();

    ScanResult getScanResult();

    FileContentsTracker getFileContentsTracker();

    DocumentationFactory getDocumentationFactory();
}
