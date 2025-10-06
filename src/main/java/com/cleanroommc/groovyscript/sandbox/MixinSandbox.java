package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.core.LaunchClassLoaderResourceCache;
import com.cleanroommc.groovyscript.sandbox.engine.*;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.*;
import groovy.lang.Binding;
import groovy.lang.Script;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.groovy.control.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.UnmodifiableView;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.Cancellable;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;

public class MixinSandbox extends AbstractGroovySandbox {

    private static MixinSandbox instance;
    private static Map<String, byte[]> injectedResourceCache = new Object2ObjectOpenHashMap<>();
    private static final List<String> earlyMixinClasses = new ArrayList<>();
    private static final List<String> lateMixinClasses = new ArrayList<>();

    @UnmodifiableView
    @ApiStatus.Internal
    public static List<String> getEarlyMixinClasses() {
        return Collections.unmodifiableList(earlyMixinClasses);
    }

    @UnmodifiableView
    @ApiStatus.Internal
    public static List<String> getLateMixinClasses() {
        return Collections.unmodifiableList(lateMixinClasses);
    }

    @ApiStatus.Internal
    public static void loadEarlyMixins() throws Exception {
        if (MixinSandbox.instance != null) {
            throw new IllegalStateException("Mixins already loaded");
        }
        MixinSandbox.instance = new MixinSandbox();
        MixinSandbox.instance.loadedClasses.clear();
        MixinSandbox.instance.run(LoadStage.MIXIN_EARLY);

        Field lclBytecodesField = Launch.classLoader.getClass().getDeclaredField("resourceCache");
        lclBytecodesField.setAccessible(true);
        //noinspection unchecked
        Map<String, byte[]> resourceCache = (Map<String, byte[]>) lclBytecodesField.get(Launch.classLoader);
        lclBytecodesField.set(Launch.classLoader, new LaunchClassLoaderResourceCache(resourceCache, MixinSandbox.injectedResourceCache));

        // called later than mixin booter, we can now try to compile groovy mixins
        // the groovy mixins need to be compiled to bytes manually first
        Collection<String> groovyMixins = MixinSandbox.instance.collectCompiledMixins();
        if (groovyMixins.isEmpty()) {
            LOG.info("No early groovy mixins configured");
            return;
        }
        // create and register config
        Mixins.addConfiguration("mixin.groovyscript.custom.early.json");
        earlyMixinClasses.addAll(groovyMixins); // mixins are registered by a mixin config plugin at core.MixinPlugin
    }

    @ApiStatus.Internal
    public static void loadLateMixins() {
        SandboxData.onLateInit();
        MixinSandbox.instance.loadedClasses.clear();
        MixinSandbox.instance.run(LoadStage.MIXIN_LATE);
        MixinSandbox.instance.getEngine().writeIndex();
        // from now on we forbid modifying the map
        MixinSandbox.injectedResourceCache = Collections.unmodifiableMap(MixinSandbox.injectedResourceCache);

        Collection<String> groovyMixins = instance.collectCompiledMixins();
        if (groovyMixins.isEmpty()) {
            LOG.info("No late groovy mixins configured");
            return;
        }
        lateMixinClasses.addAll(groovyMixins); // mixins are registered by a mixin config plugin at core.MixinPlugin

    }

    public static final Logger LOG = LogManager.getLogger("GroovyScript-MixinSandbox");
    public static final boolean DEBUG = true;

    private final Set<CompiledClass> loadedClasses = new ObjectOpenHashSet<>();

    private MixinSandbox() {}

    @Override
    protected MixinScriptEngine createEngine(CompilerConfiguration config) {
        return new MixinScriptEngine(SandboxData.getRootUrls(), SandboxData.getMixinScriptCachePath(), SandboxData.getScriptFile(), config)
                .onClassLoaded(cc -> {
                    if (injectedResourceCache.put(cc.getName(), cc.getData()) == null) {
                        if (cc.isMixin()) {
                            GroovyLog.get().info(" - loaded mixin class {}", cc.getName());
                        } else {
                            GroovyLog.get().info(" - loaded class {}", cc.getName());
                        }
                    }
                    loadedClasses.add(cc);
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
                        Share.class,
                        LocalRef.class,
                        LocalIntRef.class,
                        LocalLongRef.class,
                        LocalFloatRef.class,
                        LocalDoubleRef.class,
                        LocalBooleanRef.class,
                        LocalByteRef.class,
                        LocalShortRef.class,
                        Cancellable.class,
                        Invoker.class,
                        Accessor.class,
                        WrapMethod.class
                )
                        .stream()
                        .map(Class::getName)
                        .toArray(String[]::new));
        super.initConfig(config);
    }

    @Override
    public boolean canRunInStage(LoadStage stage) {
        return stage.isMixin();
    }

    @Override
    protected void preRun() {
        if (ScriptEngine.DELETE_CACHE_ON_RUN) getEngine().deleteScriptCache();
        GroovyLog.get().infoMC("Loading mixin scripts in loader '{}'", getCurrentLoader());
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

    @Override
    protected void loadScripts(Binding binding, Set<String> executedClasses, boolean run) throws Throwable {
        if (getCurrentLoader() == LoadStage.MIXIN_EARLY) {
            super.loadScripts(binding, executedClasses, run);
            return;
        }
        FileUtil.cleanScriptPathWarnedCache();
        Collection<File> files = getScriptFiles();
        if (files.isEmpty()) return;
        List<CompiledScript> scripts = getEngine().findScripts(files);
        if (scripts.isEmpty()) return;
        for (CompiledScript compiledScript : scripts) {
            if (!executedClasses.contains(compiledScript.getPath()) && compiledScript.requiresModLoaded()) {
                loadScript(compiledScript, binding, run);
                Class<?> clz = compiledScript.getScriptClass();
                if (!compiledScript.preprocessorCheckFailed() && clz != null && isClassScript(clz)) {
                    executedClasses.add(compiledScript.getPath());
                }
            }
        }
    }

    private Collection<String> collectCompiledMixins() {
        List<String> mixinClasses = new ArrayList<>();
        for (CompiledClass cc : this.loadedClasses) {
            if (!cc.isMixin()) continue;
            if (!cc.getName().startsWith(SandboxData.MIXIN_PKG + '.')) throw new IllegalArgumentException();
            mixinClasses.add(cc.getName().substring(SandboxData.MIXIN_PKG.length() + 1));
        }
        this.loadedClasses.clear();
        return mixinClasses;
    }

    @Override
    public Collection<File> getScriptFiles() {
        return SandboxData.getSortedFilesOf(getScriptRoot(), Collections.singleton(SandboxData.MIXIN_PKG + "/"), false);
    }

    @Override
    protected void loadScript(CompiledScript compiledScript, Binding binding, boolean run) {
        long t = System.currentTimeMillis();
        getEngine().loadScript(compiledScript);
        this.compileTime += System.currentTimeMillis() - t;
        this.loadedClasses.add(compiledScript);
    }
}
