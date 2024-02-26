package net.prominic.groovyls.util;

import io.github.classgraph.ClassInfo;
import net.prominic.groovyls.compiler.ast.ASTContext;
import org.codehaus.groovy.ast.ClassNode;
import org.jetbrains.annotations.Nullable;

public class ClassGraphUtils {

    public static @Nullable ClassInfo resolveAllowedClassInfo(ClassNode node, ASTContext context) {
        ClassInfo result = null;
        while (result == null) {
            if (node.equals(new ClassNode(Object.class)))
                return null;
            result = context.getLanguageServerContext().getScanResult().getClassInfo(node.getName());
            for (ClassNode anInterface : node.getInterfaces()) {
                result = context.getLanguageServerContext().getScanResult().getClassInfo(anInterface.getName());
                if (result != null) {
                    break;
                }
            }
            node = node.getSuperClass();
        }

        return result;
    }
}
