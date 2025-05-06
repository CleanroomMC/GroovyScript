package com.cleanroommc.groovyscript.core.mixin.groovy;

import com.cleanroommc.groovyscript.sandbox.transformer.AsmDecompileHelper;
import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.decompiled.AsmReferenceResolver;
import org.codehaus.groovy.ast.decompiled.ClassStub;
import org.codehaus.groovy.ast.decompiled.DecompiledClassNode;
import org.codehaus.groovy.control.ClassNodeResolver;
import org.codehaus.groovy.control.CompilationUnit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ClassNodeResolver.class, remap = false)
public abstract class ClassNodeResolverMixin {

    @Shadow
    private static boolean isFromAnotherClassLoader(GroovyClassLoader loader, String fileName) {
        return false;
    }

    @Shadow
    private static ClassNodeResolver.LookupResult tryAsScript(String name, CompilationUnit compilationUnit, ClassNode oldClass) {
        return null;
    }

    /**
     * @author brachy
     * @reason properly find classes
     */
    @Overwrite
    private ClassNodeResolver.LookupResult findDecompiled(final String name, final CompilationUnit compilationUnit, final GroovyClassLoader loader) {
        ClassNode node = ClassHelper.make(name);
        if (node.isResolved()) {
            return new ClassNodeResolver.LookupResult(null, node);
        }

        DecompiledClassNode asmClass = null;
        ClassStub stub = AsmDecompileHelper.findDecompiledClass(name);
        if (stub != null) {
            asmClass = new DecompiledClassNode(stub, new AsmReferenceResolver((ClassNodeResolver) (Object) this, compilationUnit));
            if (!asmClass.getName().equals(name)) {
                // this may happen under Windows because getResource is case-insensitive under that OS!
                asmClass = null;
            }
        }

        if (asmClass != null) {
            String fileName = name.replace('.', '/') + ".class";
            if (isFromAnotherClassLoader(loader, fileName)) {
                return tryAsScript(name, compilationUnit, asmClass);
            }

            return new ClassNodeResolver.LookupResult(null, asmClass);
        }
        return null;
    }
}
