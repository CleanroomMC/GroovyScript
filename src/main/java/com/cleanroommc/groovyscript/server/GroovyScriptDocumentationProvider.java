package com.cleanroommc.groovyscript.server;

import com.cleanroommc.groovyscript.api.IGroovyContainer;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.documentation.Registry;
import net.prominic.groovyls.compiler.ast.ASTContext;
import net.prominic.groovyls.compiler.documentation.IDocumentationProvider;
import net.prominic.groovyls.compiler.util.GroovyReflectionUtils;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Objects;

public class GroovyScriptDocumentationProvider implements IDocumentationProvider {

    @Override
    public @Nullable String getDocumentation(AnnotatedNode node, ASTContext context) {
        var builder = new StringBuilder();

        if (node instanceof MethodNode methodNode && methodNode.getDeclaringClass().implementsInterface(new ClassNode(IScriptReloadable.class))) {
            ModSupport.getAllContainers().stream()
                    .filter(IGroovyContainer::isLoaded)
                    .map(groovyContainer -> {
                        var methodRegistry = groovyContainer.get().getRegistries().stream()
                                .filter(registry -> registry.getClass().equals(methodNode.getDeclaringClass().getTypeClass()))
                                .findFirst();

                        if (methodRegistry.isPresent()) {
                            var method = GroovyReflectionUtils.resolveMethodFromMethodNode(methodNode, context);

                            if (method.isPresent() && method.get().isAnnotationPresent(MethodDescription.class)) {
                                return new Registry(groovyContainer, methodRegistry.get()).documentMethods(Collections.singletonList(method.get()), true);
                            }
                        }

                        return null;
                    }).filter(Objects::nonNull).forEach(builder::append);
        }

        return builder.length() == 0 ? null : builder.toString();
    }
}
