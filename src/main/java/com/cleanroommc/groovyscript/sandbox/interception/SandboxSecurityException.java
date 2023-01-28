package com.cleanroommc.groovyscript.sandbox.interception;

import com.cleanroommc.groovyscript.sandbox.GroovyScriptSandbox;
import org.kohsuke.groovy.sandbox.impl.Checker;

public class SandboxSecurityException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SandboxSecurityException(String msg) {
        super(msg);
    }

    public static SandboxSecurityException format(String msg) {
        String source = GroovyScriptSandbox.relativizeSource(Checker.getSource());
        int line = Checker.getLineNumber();
        return new SandboxSecurityException(msg + " in script '" + source + "' in line '" + line + "'!");
    }
}