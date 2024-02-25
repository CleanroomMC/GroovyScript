package net.prominic.groovyls.compiler.util;

import com.cleanroommc.groovyscript.api.IGroovyContainer;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.documentation.Registry;
import com.cleanroommc.groovyscript.sandbox.security.GroovySecurityManager;
import com.google.common.collect.Iterators;
import net.prominic.groovyls.compiler.ast.ASTContext;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.VariableExpression;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class MkDocUtils {

    public static String mkDocToMarkdownDescription(AnnotatedNode node, ASTContext context) {
        var builder = new StringBuilder();

        if (node instanceof MethodNode) {
            var methodNode = (MethodNode) node;

            if (methodNode.getDeclaringClass().implementsInterface(new ClassNode(IScriptReloadable.class))) {
                ModSupport.getAllContainers().stream()
                        .filter(IGroovyContainer::isLoaded)
                        .map(groovyContainer -> {
                            var methodRegistry = groovyContainer.get().getRegistries().stream().filter(registry -> registry.getClass().equals(methodNode.getDeclaringClass().getTypeClass())).findFirst();

                            if (methodRegistry.isPresent()) {
                                var methods = Arrays.stream(methodRegistry.get().getClass().getDeclaredMethods())
                                        .filter(GroovySecurityManager.INSTANCE::isValid)
                                        .filter(method -> method.getName().equals(methodNode.getName()) &&
                                                          method.getParameterTypes().length == methodNode.getParameters().length &&
                                                          Iterators.elementsEqual(Arrays.stream(method.getParameterTypes()).iterator(),
                                                                                  Arrays.stream(methodNode.getParameters()).map(parameter -> parameter.getType().getTypeClass()).iterator()))
                                        .filter(method -> method.isAnnotationPresent(MethodDescription.class))
                                        .collect(Collectors.toList());

                                return new Registry(groovyContainer, methodRegistry.get()).documentMethods(methods, true);
                            }

                            return null;
                        }).filter(Objects::nonNull).forEach(builder::append);
            }
        }

        return builder.length() == 0 ? null : builder.toString();
    }
}
