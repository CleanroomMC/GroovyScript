package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.INamed;
import com.cleanroommc.groovyscript.event.GroovyEventManager;
import com.cleanroommc.groovyscript.event.GroovyReloadEvent;
import com.cleanroommc.groovyscript.event.ScriptRunEvent;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.engine.CompiledScript;
import com.cleanroommc.groovyscript.sandbox.engine.ScriptEngine;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.Script;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraftforge.common.MinecraftForge;
import org.apache.groovy.internal.util.UncheckedThrow;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.InvokerInvocationException;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractGroovySandbox {

    private final ScriptEngine engine;

    private String currentScript;
    private LoadStage currentLoadStage;

    private final ThreadLocal<Boolean> running = ThreadLocal.withInitial(() -> false);
    private final Map<String, Object> bindings = new Object2ObjectOpenHashMap<>();
    private final ImportCustomizer importCustomizer = new ImportCustomizer();
    private final Map<List<StackTraceElement>, AtomicInteger> storedExceptions = new Object2ObjectOpenHashMap<>();

    protected long compileTime;
    protected long runTime;

    public AbstractGroovySandbox() {
        CompilerConfiguration config = new CompilerConfiguration();
        initConfig(config);
        this.engine = createEngine(config);
    }

    protected abstract ScriptEngine createEngine(CompilerConfiguration config);

    protected Binding createBindings() {
        Binding binding = new Binding(this.bindings);
        postInitBindings(binding);
        return binding;
    }

    public Map<String, Object> getGlobals() {
        return this.bindings;
    }

    @Deprecated
    public void registerBinding(String name, Object obj) {
        registerGlobal(name, obj);
    }

    @Deprecated
    public void registerBinding(INamed named) {
        registerGlobal(named);
    }

    public void registerGlobal(String name, Object obj) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(obj);
        for (String alias : Alias.generateOf(name)) {
            bindings.put(alias, obj);
        }
    }

    public void registerGlobal(INamed named) {
        Objects.requireNonNull(named);
        for (String alias : named.getAliases()) {
            bindings.put(alias, named);
        }
    }

    public abstract boolean canRunInStage(LoadStage stage);

    public void run(LoadStage currentLoadStage) {
        if (!canRunInStage(currentLoadStage)) {
            throw new IllegalArgumentException("The current sandbox can not run in load stage " + currentLoadStage);
        }
        this.currentLoadStage = Objects.requireNonNull(currentLoadStage);
        try {
            load();
        } catch (IOException | ScriptException | ResourceException e) {
            GroovyLog.get().exception("An exception occurred while trying to run groovy code! This is might be a internal groovy issue.", e);
        } catch (Throwable t) {
            GroovyLog.get().exception(t);
        } finally {
            GroovyLog.get().infoMC("Groovy scripts took {}ms to compile and {}ms to run in {}.", this.compileTime, this.runTime, currentLoadStage.getName());
            this.currentLoadStage = null;
            if (currentLoadStage == LoadStage.POST_INIT) {
                engine.writeIndex();
            }
        }
    }

    protected void runScript(Script script) throws Throwable {
        GroovyLog.get().info(" - running script {}", script.getClass().getName());
        setCurrentScript(script.getClass().getName());
        try {
            script.run();
        } finally {
            setCurrentScript(null);
        }
    }

    protected void runClass(Class<?> script) throws Throwable {
        GroovyLog.get().info(" - loading class {}", script.getName());
        setCurrentScript(script.getName());
        try {
            // $getLookup is present on all groovy created classes
            // call it cause the class to be initialised
            Method m = script.getMethod("$getLookup");
            m.invoke(null);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            GroovyLog.get().errorMC("Error initialising class '{}'", script);
        } finally {
            setCurrentScript(null);
        }
    }

    public void checkSyntax() {
        Binding binding = createBindings();
        Set<String> executedClasses = new ObjectOpenHashSet<>();

        for (LoadStage loadStage : LoadStage.getLoadStages()) {
            GroovyLog.get().info("Checking syntax in loader '{}'", this.currentLoadStage);
            this.currentLoadStage = loadStage;
            try {
                load(binding, executedClasses, false);
            } catch (Throwable e) {
                GroovyLog.get().exception(e);
            }
        }
    }

    @ApiStatus.Internal
    public <T> T runClosure(Closure<T> closure, Object... args) {
        boolean wasRunning = isRunning();
        if (!wasRunning) startRunning();
        T result = null;
        try {
            result = runClosureInternal(closure, args);
        } catch (Throwable t) {
            List<StackTraceElement> stackTrace = Arrays.asList(t.getStackTrace());
            AtomicInteger counter = this.storedExceptions.get(stackTrace);
            if (counter == null) {
                GroovyLog.get().exception("An exception occurred while running a closure at least once!", t);
                this.storedExceptions.put(stackTrace, new AtomicInteger(1));
                UncheckedThrow.rethrow(t);
                return null; // unreachable statement
            } else {
                counter.getAndIncrement();
            }
        } finally {
            if (!wasRunning) stopRunning();
        }
        return result;
    }

    @GroovyBlacklist
    private static <T> T runClosureInternal(Closure<T> closure, Object[] args) throws Throwable {
        // original Closure.call(Object... arguments) code
        try {
            //noinspection unchecked
            return (T) closure.getMetaClass().invokeMethod(closure, "doCall", args);
        } catch (InvokerInvocationException e) {
            throw e.getCause();
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw e;
            } else {
                throw new GroovyRuntimeException(e.getMessage(), e);
            }
        }
    }

    private void load() throws Throwable {
        preRun();

        Binding binding = createBindings();
        Set<String> executedClasses = new ObjectOpenHashSet<>();

        this.running.set(true);
        try {
            load(binding, executedClasses, true);
        } finally {
            this.running.set(false);
            postRun();
            setCurrentScript(null);
        }
    }

    protected void load(Binding binding, Set<String> executedClasses, boolean run) throws Throwable {
        this.compileTime = 0L;
        this.runTime = 0L;
        // now run all script files
        loadScripts(binding, executedClasses, run);
    }

    protected void loadScripts(Binding binding, Set<String> executedClasses, boolean run) throws Throwable {
        Collection<File> files = getScriptFiles();
        List<CompiledScript> scripts = this.engine.findScripts(files);
        for (CompiledScript compiledScript : scripts) {
            if (!executedClasses.contains(compiledScript.getPath())) {
                loadScript(compiledScript, binding, run);
                Class<?> clz = compiledScript.getScriptClass();
                if (!compiledScript.preprocessorCheckFailed() && clz != null && isClassScript(clz)) {
                    executedClasses.add(compiledScript.getPath());
                }
            }
        }
    }

    protected void loadScript(CompiledScript compiledScript, Binding binding, boolean run) throws Throwable{
        long t = System.currentTimeMillis();
        this.engine.loadScript(compiledScript);
        this.compileTime += System.currentTimeMillis() - t;
        if (compiledScript.preprocessorCheckFailed()) return;
        if (compiledScript.getScriptClass() == null) {
            GroovyLog.get().errorMC("Error loading script {}", compiledScript.getPath());
            return;
        }
        if (!isClassScript(compiledScript.getScriptClass())) {
            // script is a class
            if (run && shouldRunFile(compiledScript.getPath())) {
                t = System.currentTimeMillis();
                runClass(compiledScript.getScriptClass());
                this.runTime += System.currentTimeMillis() - t;
            }
            return;
        }
        if (run && shouldRunFile(compiledScript.getPath())) {
            Script script = InvokerHelper.createScript(compiledScript.getScriptClass(), binding);
            t = System.currentTimeMillis();
            runScript(script);
            this.runTime += System.currentTimeMillis() - t;
        }
    }

    protected void startRunning() {
        this.running.set(true);
    }

    protected void stopRunning() {
        this.running.set(false);
    }

    @ApiStatus.OverrideOnly
    protected void postInitBindings(Binding binding) {
        binding.setProperty("out", GroovyLog.get().getWriter());
        binding.setVariable("globals", getBindings());
    }

    @ApiStatus.OverrideOnly
    protected void initConfig(CompilerConfiguration config) {
        config.addCompilationCustomizers(getImportCustomizer());
    }

    @ApiStatus.OverrideOnly
    protected void preRun() {
        if (ScriptEngine.DELETE_CACHE_ON_RUN) this.engine.deleteScriptCache();
        // first clear all added events
        GroovyEventManager.INSTANCE.reset();
        if (this.currentLoadStage.isReloadable() && !ReloadableRegistryManager.isFirstLoad()) {
            // if this is not the first time this load stage is executed, reload all virtual registries
            ReloadableRegistryManager.onReload();
            // invoke reload event
            MinecraftForge.EVENT_BUS.post(new GroovyReloadEvent());
        }
        GroovyLog.get().infoMC("Running scripts in loader '{}'", this.currentLoadStage);
        // this.engine.prepareEngine(this.currentLoadStage);
        // and finally invoke pre script run event
        MinecraftForge.EVENT_BUS.post(new ScriptRunEvent.Pre(this.currentLoadStage));
    }

    @ApiStatus.OverrideOnly
    protected boolean shouldRunFile(String file) {
        return true;
    }

    @ApiStatus.OverrideOnly
    protected void postRun() {
        if (this.currentLoadStage == LoadStage.POST_INIT) {
            ReloadableRegistryManager.afterScriptRun();
        }
        MinecraftForge.EVENT_BUS.post(new ScriptRunEvent.Post(this.currentLoadStage));
        if (this.currentLoadStage == LoadStage.POST_INIT && ReloadableRegistryManager.isFirstLoad()) {
            ReloadableRegistryManager.setLoaded();
        }
    }

    public File getScriptRoot() {
        return getEngine().getScriptRoot();
    }

    public Collection<File> getScriptFiles() {
        return GroovyScript.getRunConfig().getSortedFiles(getScriptRoot(), this.currentLoadStage.getName());
    }

    public boolean isRunning() {
        return this.running.get();
    }

    public Map<String, Object> getBindings() {
        return bindings;
    }

    public ImportCustomizer getImportCustomizer() {
        return importCustomizer;
    }

    public ScriptEngine getEngine() {
        return engine;
    }

    public String getCurrentScript() {
        return currentScript;
    }

    protected void setCurrentScript(String currentScript) {
        this.currentScript = currentScript;
    }

    public LoadStage getCurrentLoader() {
        return currentLoadStage;
    }

    public long getLastCompileTime() {
        return compileTime;
    }

    public long getLastRunTime() {
        return runTime;
    }

    public static boolean isClassScript(Class<?> clazz) {
        return Script.class.isAssignableFrom(clazz);
    }
}
