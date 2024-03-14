package net.prominic.groovyls.compiler.util;

import com.cleanroommc.groovyscript.sandbox.security.GroovySecurityManager;
import com.google.common.collect.Iterators;
import io.github.classgraph.MethodInfo;
import net.prominic.groovyls.compiler.ast.ASTContext;
import org.codehaus.groovy.ast.MethodNode;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public class GroovyReflectionUtils {

    public static Optional<Method> resolveMethodFromMethodNode(MethodNode methodNode, ASTContext context) {
        return Arrays.stream(methodNode.getDeclaringClass().getTypeClass().getMethods())
                .filter(GroovySecurityManager.INSTANCE::isValid)
                .filter(method -> method.getName().equals(methodNode.getName()) &&
                                  method.getParameterTypes().length == methodNode.getParameters().length &&
                                  Iterators.elementsEqual(Arrays.stream(method.getParameterTypes()).iterator(),
                                                          Arrays.stream(methodNode.getParameters()).map(parameter -> parameter.getType().getTypeClass()).iterator()))
                .findFirst();
    }

    public static Optional<Method> resolveMethodFromMethodInfo(MethodInfo methodInfo, ASTContext context) {
        return Arrays.stream(methodInfo.getClassInfo().loadClass().getMethods())
                .filter(GroovySecurityManager.INSTANCE::isValid)
                .filter(method -> method.getName().equals(methodInfo.getName()) &&
                                  method.getParameterTypes().length == methodInfo.getParameterInfo().length &&
                                  Iterators.elementsEqual(Arrays.stream(method.getParameterTypes()).iterator(),
                                                          Arrays.stream(methodInfo.getParameterInfo()).map(parameter -> context.getLanguageServerContext().getScanResult()
                                                                  .loadClass(parameter.getTypeSignatureOrTypeDescriptor().toString(), true)).iterator()))
                .findFirst();
    }
}
