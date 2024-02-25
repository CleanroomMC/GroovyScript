package net.prominic.groovyls.compiler;

import com.cleanroommc.groovyscript.sandbox.GroovySandbox;
import io.github.classgraph.ScanResult;

public interface ILanguageServerContext {

    GroovySandbox getSandbox();

    ScanResult getScanResult();
}
