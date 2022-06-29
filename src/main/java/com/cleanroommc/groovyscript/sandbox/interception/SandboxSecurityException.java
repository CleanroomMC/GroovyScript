package com.cleanroommc.groovyscript.sandbox.interception;

public class SandboxSecurityException extends Exception {

    private static final long serialVersionUID = 1L;

    public SandboxSecurityException(String msg) {
        super(msg);
    }

    public SandboxSecurityException(Type type, String target, String method) {
        super(createMsg(type, target, method));
    }

    private static String createMsg(Type type, String target, String method) {
        if (type == Type.CONSTRUCTOR) {
            return "Prohibited constructor call on class '" + target + "'!";
        }
        if (type == Type.CLASS) {
            return "Prohibited class reference at '" + target + "'!";
        }
        return "Prohibited method call '" + method + "' on class '" + target + "'!";
    }

    public enum Type {
        CONSTRUCTOR, METHOD, CLASS
    }

}