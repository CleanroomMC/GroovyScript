package org.kohsuke.groovy.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.util.GroovyScriptEngine;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * @author brachy84
 */
public abstract class GroovySandbox {

    private static final ThreadLocal<GroovySandbox> currentSandbox = new ThreadLocal<>();

    @Nullable
    public static GroovySandbox getCurrentSandbox() {
        return currentSandbox.get();
    }

    @NotNull
    public static List<GroovyInterceptor> getInterceptors() {
        GroovySandbox sandbox = getCurrentSandbox();
        return sandbox != null ? sandbox.interceptors : Collections.emptyList();
    }

    private final List<GroovyInterceptor> interceptors = new ArrayList<>();
    private final URL[] scriptEnvironment;
    private final ThreadLocal<Boolean> running = ThreadLocal.withInitial(() -> false);
    private final Map<String, Object> bindings = new Object2ObjectOpenHashMap<>();

    protected GroovySandbox(URL[] scriptEnvironment) {
        if (scriptEnvironment == null || scriptEnvironment.length == 0) {
            throw new NullPointerException("Script Environment must be non null and at least contain one URL!");
        }
        this.scriptEnvironment = scriptEnvironment;
    }

    protected GroovySandbox(List<URL> scriptEnvironment) {
        this(scriptEnvironment.toArray(new URL[0]));
    }

    public void registerBinding(String name, Object obj) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(obj);
        bindings.put(name, obj);
    }

    public void registerInterceptor(GroovyInterceptor interceptor) {
        Objects.requireNonNull(interceptor);
        interceptors.add(interceptor);
    }

    public void run() throws Exception {
        currentSandbox.set(this);
        preRun();

        GroovyScriptEngine engine = new GroovyScriptEngine(scriptEnvironment);
        CompilerConfiguration config = new CompilerConfiguration(CompilerConfiguration.DEFAULT);
        engine.setConfig(config);
        initEngine(engine, config);
        Binding binding = new Binding(bindings);
        postInitBindings(binding);

        running.set(true);
        try {
            for (File file : getScriptFiles()) {
                if (shouldRunFile(file)) {
                    engine.run(file.toString(), binding);
                }
            }
        } finally {
            running.set(false);
            postRun();
            currentSandbox.set(null);
        }
    }

    public <T> T runClosure(Closure<T> closure, Object... args) {
        currentSandbox.set(this);
        running.set(true);
        T result = null;
        try {
            result = closure.call(args);
        } catch (Exception e) {
            GroovyScript.LOGGER.error("Caught an exception trying to run a closure:");
            e.printStackTrace();
        } finally {
            running.set(false);
            currentSandbox.set(null);
        }
        return result;
    }

    @ApiStatus.OverrideOnly
    protected void postInitBindings(Binding binding) {
    }

    @ApiStatus.OverrideOnly
    protected void initEngine(GroovyScriptEngine engine, CompilerConfiguration config) {
    }

    @ApiStatus.OverrideOnly
    protected void preRun() {
    }

    @ApiStatus.OverrideOnly
    protected boolean shouldRunFile(File file) {
        return true;
    }

    @ApiStatus.OverrideOnly
    protected void postRun() {
    }

    public abstract Iterable<File> getScriptFiles();

    public boolean isRunning() {
        return this.running.get();
    }
}
