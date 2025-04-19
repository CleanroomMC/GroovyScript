package com.cleanroommc.groovyscript.server;

import com.cleanroommc.groovyscript.api.IGroovyContainer;
import com.cleanroommc.groovyscript.api.IScriptReloadable;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.documentation.Registry;
import com.cleanroommc.groovyscript.documentation.helper.descriptor.MethodAnnotation;
import net.prominic.groovyls.compiler.ast.ASTContext;
import net.prominic.groovyls.compiler.documentation.IDocumentationProvider;
import net.prominic.groovyls.compiler.util.GroovyReflectionUtils;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.MethodNode;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class GroovyScriptDocumentationProvider implements IDocumentationProvider {

    @Override
    public @Nullable String getDocumentation(AnnotatedNode node, ASTContext context) {
        var builder = new StringBuilder();

        if (node instanceof MethodNode methodNode && methodNode.getDeclaringClass().implementsInterface(ClassHelper.makeCached(IScriptReloadable.class))) {
            ModSupport.getAllContainers().stream().filter(IGroovyContainer::isLoaded).map(groovyContainer -> {
                var methodRegistry = groovyContainer.get()
                        .getRegistries()
                        .stream()
                        .filter(registry -> registry.getClass().equals(methodNode.getDeclaringClass().getTypeClass()))
                        .findFirst();

                if (methodRegistry.isPresent()) {
                    var method = GroovyReflectionUtils.resolveMethodFromMethodNode(methodNode, context);

                    if (method != null && method.isAnnotationPresent(MethodDescription.class)) {
                        return new Registry(groovyContainer, methodRegistry.get())
                                .methodDescription(new MethodAnnotation<>(method, method.getAnnotation(MethodDescription.class)));
                    }
                }

                return null;
            }).filter(Objects::nonNull).forEach(builder::append);
        }

        return builder.length() == 0 ? null : builder.toString();
    }

    @Override
    public @Nullable String getSortText(AnnotatedNode node, ASTContext context) {
        return node instanceof MethodNode methodNode && !methodNode.getDeclaringClass().getAnnotations(ClassHelper.makeCached(RegistryDescription.class)).isEmpty() && !methodNode.getAnnotations(ClassHelper.makeCached(MethodDescription.class)).isEmpty() ? "!!!" + methodNode.getName() : null;
    }
}
