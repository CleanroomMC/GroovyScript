package com.cleanroommc.groovyscript.sandbox.expand;

import org.apache.commons.lang3.StringUtils;

public interface IDocumented {

    String getDocumentation();

    static String toJavaDoc(String s) {
        if (s == null || s.isEmpty()) return StringUtils.EMPTY;
        if (s.startsWith("/**") && s.endsWith("*/")) return s;
        return "/**\n *" + s.replaceAll("\n", "\n * ") + "\n*/";
    }

}
