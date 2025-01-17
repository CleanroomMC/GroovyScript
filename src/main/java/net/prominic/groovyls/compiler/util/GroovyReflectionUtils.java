package net.prominic.groovyls.compiler.util;

import com.cleanroommc.groovyscript.sandbox.security.GroovySecurityManager;
import io.github.classgraph.MethodInfo;
import net.prominic.groovyls.compiler.ast.ASTContext;
import org.codehaus.groovy.ast.MethodNode;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.function.Function;

public class GroovyReflectionUtils {

    public static @Nullable Method resolveMethodFromMethodNode(MethodNode methodNode, ASTContext context) {
        for (Method m : methodNode.getDeclaringClass().getTypeClass().getMethods()) {
            if (!GroovySecurityManager.INSTANCE.isValid(m) || !methodNode.getName().equals(m.getName()) || methodNode.getParameters().length != m.getParameterCount()) {
                continue;
            }
            if (matchesParams(m.getParameterTypes(), methodNode.getParameters(), p -> p.getType().getTypeClass())) {
                return m;
            }
        }
        return null;
    }

    public static @Nullable Method resolveMethodFromMethodInfo(MethodInfo methodInfo, ASTContext context) {
        for (Method m : methodInfo.getClassInfo().loadClass().getMethods()) {
            if (!GroovySecurityManager.INSTANCE.isValid(m) || !methodInfo.getName().equals(m.getName()) || methodInfo.getParameterInfo().length != m.getParameterCount()) {
                continue;
            }
            if (matchesParams(
                    m.getParameterTypes(),
                    methodInfo.getParameterInfo(),
                    p -> context.getLanguageServerContext()
                            .getScanResult()
                            .loadClass(
                                    p.getTypeSignatureOrTypeDescriptor().toString(),
                                    true))) {
                return m;
            }
        }
        return null;
    }

    private static <T> boolean matchesParams(Class<?>[] params, T[] otherParams, Function<T, Class<?>> toClass) {
        for (int i = 0, n = params.length; i < n; i++) {
            if (!params[i].equals(toClass.apply(otherParams[i]))) return false;
        }
        return true;
    }
}
