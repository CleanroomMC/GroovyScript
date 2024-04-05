package com.cleanroommc.groovyscript.sandbox.expand;

public interface IDocumented {

    String getDocumentation();

    static String toJavaDoc(String s) {
        if (s.startsWith("/**") && s.endsWith("*/")) return s;
        return "/**\n *" + s.replaceAll("\n", "\n * ") + "\n*/";
    }
}
