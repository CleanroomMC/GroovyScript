package org.kohsuke.groovy.sandbox;

import org.codehaus.groovy.ast.ClassNode;

public class AliasClassNode extends ClassNode {

    private final String aliasName;

    public AliasClassNode(Class<?> c, String aliasName) {
        super(c);
        this.aliasName = aliasName;
    }

    public String getAliasName() {
        return aliasName;
    }
}
