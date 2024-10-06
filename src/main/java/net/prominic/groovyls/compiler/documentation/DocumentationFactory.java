package net.prominic.groovyls.compiler.documentation;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.prominic.groovyls.compiler.ast.ASTContext;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.MethodNode;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class DocumentationFactory {

    private final IDocumentationProvider[] providers;
    private final Set<String> lastSortedMethods = new ObjectOpenHashSet<>();

    public DocumentationFactory(IDocumentationProvider... providers) {
        this.providers = providers;
        this.lastSortedMethods.add("hashCode");
        this.lastSortedMethods.add("equals");
        this.lastSortedMethods.add("wait");
        this.lastSortedMethods.add("getClass");
        this.lastSortedMethods.add("notify");
        this.lastSortedMethods.add("notifyAll");
        this.lastSortedMethods.add("toString");
    }

    public @Nullable String getDocumentation(AnnotatedNode node, ASTContext context) {
        for (IDocumentationProvider provider : providers) {
            String documentation = provider.getDocumentation(node, context);
            if (documentation != null) {
                return documentation;
            }
        }
        return null;
    }

    public @Nullable String getSortText(AnnotatedNode node, ASTContext context) {
        for (IDocumentationProvider provider : providers) {
            String documentation = provider.getSortText(node, context);
            if (documentation != null) {
                return documentation;
            }
        }
        if (node instanceof MethodNode mn) {
            if (node.getDeclaringClass().isDerivedFrom(ClassHelper.makeCached(GroovyPropertyContainer.class)) || node.getDeclaringClass().isDerivedFrom(ClassHelper.makeCached(ModSupport.class))) {
                return "z" + mn.getName();
            }
            if (this.lastSortedMethods.contains(mn.getName())) return "zz" + mn.getName();
        }
        return null;
    }
}
