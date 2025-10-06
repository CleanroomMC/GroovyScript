package com.cleanroommc.groovyscript.sandbox.engine;

import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.SourceUnit;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

@ApiStatus.Internal
public class MixinScriptEngine extends ScriptEngine {

    private static final ClassNode MIXIN_NODE = ClassHelper.makeCached(Mixin.class);

    private Consumer<CompiledClass> onClassLoaded;

    public MixinScriptEngine(URL[] scriptEnvironment, File cacheRoot, File scriptRoot,
                             CompilerConfiguration config) {
        super(scriptEnvironment, cacheRoot, scriptRoot, config);
    }

    @Override
    protected ScriptClassLoader createClassLoader() {
        return new MixinScriptClassLoader(MixinScriptEngine.class.getClassLoader(), getConfig(), Collections.unmodifiableMap(getLoadedClasses()));
    }

    public MixinScriptEngine onClassLoaded(Consumer<CompiledClass> onClassLoaded) {
        this.onClassLoaded = onClassLoaded;
        return this;
    }

    @Override
    protected void onClassLoaded(CompiledClass cc) {
        if (this.onClassLoaded != null) {
            this.onClassLoaded.accept(cc);
        }
    }

    @Override
    protected @NotNull CompiledClass findCompiledClass(SourceUnit su, ClassNode classNode, byte[] bytecode) {
        CompiledClass cc = super.findCompiledClass(su, classNode, bytecode);
        cc.mixin = isMixinClass(classNode);
        return cc;
    }

    @Override
    protected void onClassCompiled(CompiledClass cc, ClassNode classNode, byte[] bytecode, Class<?> clz) {
        super.onClassCompiled(cc, classNode, bytecode, clz);
    }

    public static boolean isMixinClass(ClassNode classNode) {
        for (AnnotationNode annotationNode : classNode.getAnnotations()) {
            if (MIXIN_NODE.equals(annotationNode.getClassNode())) {
                return true;
            }
        }
        return false;
    }

    public class MixinClassCollector extends GroovyScriptClassLoader.ClassCollector {

        protected MixinClassCollector(GroovyScriptClassLoader cl, CompilationUnit unit, SourceUnit su) {
            super(cl, unit, su);
        }

        @Override
        protected Class<?> generateClass(byte[] code, ClassNode classNode) {
            CompiledClass cc = findCompiledClass(this.su, classNode, code);
            //Class<?> clz = super.generateClass(code, classNode);
            onClassCompiled(cc, classNode, code, null);
            return null;
        }
    }

    protected class MixinScriptClassLoader extends ScriptEngine.ScriptClassLoader {

        public MixinScriptClassLoader(ClassLoader loader, CompilerConfiguration config, Map<String, CompiledClass> cache) {
            super(loader, config, cache);
        }

        @Override
        protected MixinClassCollector createCustomCollector(CompilationUnit unit, SourceUnit su) {
            return new MixinClassCollector(new InnerLoader(this), unit, su);
        }
    }
}
