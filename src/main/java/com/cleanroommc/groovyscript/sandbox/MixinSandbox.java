package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.sandbox.engine.CompiledClass;
import com.cleanroommc.groovyscript.sandbox.engine.CompiledScript;
import com.cleanroommc.groovyscript.sandbox.engine.ScriptEngine;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import groovy.lang.Binding;
import groovy.lang.Script;
import groovyjarjarasm.asm.ClassVisitor;
import groovyjarjarasm.asm.ClassWriter;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.classgen.GeneratorContext;
import org.codehaus.groovy.control.*;
import org.codehaus.groovy.control.customizers.CompilationCustomizer;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.extensibility.IMixinConfig;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.transformer.Config;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

public class MixinSandbox extends AbstractGroovySandbox {

    @ApiStatus.Internal
    public static void loadMixins() {
        if (instance != null) {
            throw new IllegalStateException("Mixins already loaded");
        }
        instance = new MixinSandbox();
        instance.run(LoadStage.MIXIN);

        // called later than mixin booter, we can now try to compile groovy mixins
        // the groovy mixins need to be compiled to bytes manually first
        Collection<String> groovyMixins = instance.collectCompiledMixins();
        if (groovyMixins.isEmpty()) {
            LOG.info("No groovy mixins configured");
            return;
        }
        String cfgName = "mixin.groovyscript.custom.json";
        // create and register config
        Mixins.addConfiguration(cfgName);
        // obtain the just created config
        IMixinConfig config = Config.create(cfgName, MixinEnvironment.getDefaultEnvironment()).getConfig();
        List<String> mixinClasses;
        try {
            Class<?> mixinConfigClass = Class.forName("org.spongepowered.asm.mixin.transformer.MixinConfig");
            Field field = mixinConfigClass.getDeclaredField("mixinClasses");
            field.setAccessible(true);
            mixinClasses = (List<String>) field.get(config);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        // inject loaded mixin classes into configuration
        mixinClasses.addAll(groovyMixins);
        instance.getEngine().writeIndex();
    }

    private static MixinSandbox instance;
    private static final Map<String, byte[]> resourceCache;

    static {
        Map<String, byte[]> resourceCache1;
        try {
            Field field = LaunchClassLoader.class.getDeclaredField("resourceCache");
            field.setAccessible(true);
            resourceCache1 = (Map<String, byte[]>) field.get(Launch.classLoader);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        resourceCache = resourceCache1;
    }

    public static final Logger LOG = LogManager.getLogger("GroovyScript-MixinSandbox");
    public static final boolean DEBUG = true;

    private MixinSandbox() {}

    @Override
    protected ScriptEngine createEngine(CompilerConfiguration config) {
        return new ScriptEngine(SandboxData.getRootUrls(), SandboxData.getMixinScriptCachePath(), SandboxData.getScriptFile(), config)
                .onClassLoaded(cc -> {
                    GroovyLog.get().info(" - loaded mixin class {} from cache", cc.getName());
                    resourceCache.put(cc.getName(), cc.getData());
                });
    }

    @Override
    protected void initConfig(CompilerConfiguration config) {
        getImportCustomizer().addImports(
                Arrays.asList(
                        Mixin.class,
                        Inject.class,
                        At.class,
                        CallbackInfo.class,
                        CallbackInfoReturnable.class,
                        Coerce.class,
                        Constant.class,
                        Desc.class,
                        Descriptors.class,
                        Group.class,
                        ModifyArg.class,
                        ModifyArgs.class,
                        ModifyConstant.class,
                        ModifyVariable.class,
                        Redirect.class,
                        Slice.class,
                        Surrogate.class,
                        Final.class,
                        Mutable.class,
                        Overwrite.class,
                        Pseudo.class,
                        Shadow.class,
                        Unique.class,
                        SoftOverride.class,
                        Implements.class,
                        Interface.class,
                        Intrinsic.class,
                        WrapOperation.class,
                        ModifyExpressionValue.class,
                        ModifyReceiver.class,
                        ModifyReturnValue.class,
                        Local.class,
                        Share.class)
                        .stream()
                        .map(Class::getName)
                        .toArray(String[]::new));
        super.initConfig(config);
        config.addCompilationCustomizers(new CallbackInjector());
    }

    @Override
    public boolean canRunInStage(LoadStage stage) {
        return stage.isMixin();
    }

    @Override
    protected void preRun() {
        if (ScriptEngine.DELETE_CACHE_ON_RUN) getEngine().deleteScriptCache();
        GroovyLog.get().infoMC("Running scripts in loader '{}'", getCurrentLoader());
    }

    @Override
    protected void postRun() {}

    @Override
    protected void runScript(Script script) throws Throwable {
        throw new UnsupportedOperationException("Mixin scripts can not be run!");
    }

    @Override
    protected void runClass(Class<?> script) throws Throwable {
        throw new UnsupportedOperationException("Mixin scripts can not be run!");
    }

    private Collection<String> collectCompiledMixins() {
        List<String> mixinClasses = new ArrayList<>();
        Iterator<CompiledClass> it = getEngine().classIterator();
        while (it.hasNext()) {
            CompiledClass compiledClass = it.next();
            if (compiledClass.hasData()) {
                if (DEBUG) LOG.info("found groovy mixin class {}", compiledClass.getName());
                mixinClasses.add(compiledClass.getName().substring(SandboxData.MIXIN_PKG.length() + 1));
            }
        }
        return mixinClasses;
    }

    @Override
    public Collection<File> getScriptFiles() {
        return SandboxData.getSortedFilesOf(getScriptRoot(), Collections.singleton(SandboxData.MIXIN_PKG + "/"));
    }

    @Override
    protected void loadScript(CompiledScript compiledScript, Binding binding, boolean run) {
        long t = System.currentTimeMillis();
        getEngine().loadScript(compiledScript);
        this.compileTime += System.currentTimeMillis() - t;
    }

    private class ClassGenerator implements CompilationUnit.ClassgenCallback {

        private final SourceUnit su;

        private ClassGenerator(SourceUnit su) {
            this.su = su;
        }

        @Override
        public void call(ClassVisitor classVisitor, ClassNode classNode) throws CompilationFailedException {
            // mixin will try to find the bytes in that map, so we will put them there
            byte[] code = ((ClassWriter) classVisitor).toByteArray();
            resourceCache.put(classNode.getName(), code);
            if (DEBUG) LOG.info("Generated groovy mixin class {}", classNode.getName());
            MixinSandbox.this.getEngine().onCompileClass(this.su, classNode, null, code);
        }
    }

    private class CallbackInjector extends CompilationCustomizer {

        public CallbackInjector() {
            super(CompilePhase.CANONICALIZATION);
        }

        public void call(CompilationUnit cu, SourceUnit su, ClassNode classNode) throws CompilationFailedException {
            if (cu.getClassgenCallback() instanceof ClassGenerator) {
                // this is a test to see if it is called multiple times on the same unit
                GroovyLog.get().infoMC(" overwriting ClassGenerator with su {} and class {}", su.getName(), classNode.getName());
            }
            cu.setClassgenCallback(new ClassGenerator(su));
        }

        private void changeBugText(final GroovyBugError e, final SourceUnit context, CompilationUnit unit) {
            e.setBugText(
                    "exception in phase '" + unit.getPhaseDescription() + "' in source unit '" + (context != null
                            ? context.getName()
                            : "?") + "' " + e.getBugText());
        }

        @Override
        public void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
            // we cant use this since we need the compile unit
            throw new UnsupportedOperationException();
        }

        @Override
        public void doPhaseOperation(final CompilationUnit unit) throws CompilationFailedException {
            // is all this looping really needed?
            for (ModuleNode module : unit.getAST().getModules()) {
                for (ClassNode classNode : module.getClasses()) {
                    SourceUnit context = null;
                    context = classNode.getModule().getContext();
                    if (context == null || context.getPhase() < unit.getPhase() || (context.getPhase() == unit.getPhase() && !context.isPhaseComplete())) {
                        call(unit, context, classNode);
                    }
                }
            }
        }
    }
}
