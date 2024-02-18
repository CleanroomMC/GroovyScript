package com.cleanroommc.groovyscript.server;

import com.cleanroommc.groovyscript.sandbox.security.GroovySecurityManager;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import net.minecraft.launchwrapper.Launch;

public class GroovyCompiler {

    private final GroovyFiles files;
    private final GroovyServer server;

    public GroovyCompiler(GroovyServer server) {
        this.files = server.files;
        this.server = server;
    }

    public ScanResult scanResult = new ClassGraph()
            .enableClassInfo()
            .enableSystemJarsAndModules()
            .overrideClassLoaders(Launch.classLoader)
            .acceptPaths("*")
            .rejectClasses(GroovySecurityManager.INSTANCE.getBannedClasses().stream().map(Class::getName).toArray(String[]::new))
            .rejectPackages(GroovySecurityManager.INSTANCE.getBannedPackages().stream().toArray(String[]::new))
            .acceptClasses(GroovySecurityManager.INSTANCE.getWhiteListedClasses().stream().map(Class::getName).toArray(String[]::new))
            .scan();
}
