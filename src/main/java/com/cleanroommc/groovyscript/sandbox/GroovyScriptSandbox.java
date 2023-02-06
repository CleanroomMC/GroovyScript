package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.event.GroovyEventManager;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import net.minecraftforge.common.MinecraftForge;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.jetbrains.annotations.ApiStatus;
import org.kohsuke.groovy.sandbox.GroovySandbox;
import org.kohsuke.groovy.sandbox.SandboxTransformer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class GroovyScriptSandbox extends GroovySandbox {

    public static final String LOADER_PRE_INIT = "preInit";
    public static final String LOADER_POST_INIT = "postInit";
    private String currentLoader;

    public GroovyScriptSandbox(URL... scriptEnvironment) {
        super(scriptEnvironment);
        registerInterceptor(new SimpleGroovyInterceptor());
        registerBinding("mods", ModSupport.INSTANCE);
        registerBinding("log", GroovyLog.get());
        registerBinding("EventManager", GroovyEventManager.INSTANCE);
        registerBinding("eventManager", GroovyEventManager.INSTANCE);
        registerBinding("event_manager", GroovyEventManager.INSTANCE);
    }

    public Throwable run(String currentLoader) {
        this.currentLoader = Objects.requireNonNull(currentLoader);
        try {
            super.run();
            return null;
        } catch (IOException | ScriptException | ResourceException e) {
            GroovyLog.get().errorMC("An Exception occurred trying to run groovy!");
            GroovyScript.LOGGER.throwing(e);
            return e;
        } catch (Exception e) {
            GroovyLog.get().exception(e);
            return e;
        } finally {
            this.currentLoader = null;
        }
    }

    @ApiStatus.Internal
    @Override
    public void run() throws Exception {
        throw new UnsupportedOperationException("Use run(String loader) instead!");
    }

    @Override
    protected void postInitBindings(Binding binding) {
        binding.setProperty("out", GroovyLog.get().getWriter());
    }

    @Override
    protected void initEngine(GroovyScriptEngine engine, CompilerConfiguration config) {
        config.addCompilationCustomizers(new SandboxTransformer());
    }

    @Override
    protected void preRun() {
        GroovyLog.get().info("Running scripts in loader '{}'", this.currentLoader);
        MinecraftForge.EVENT_BUS.post(new ScriptRunEvent.Pre());
        if (!LOADER_PRE_INIT.equals(this.currentLoader) && !ReloadableRegistryManager.isFirstLoad()) {
            ReloadableRegistryManager.onReload();
            MinecraftForge.EVENT_BUS.post(new GroovyReloadEvent());
        }
        GroovyEventManager.INSTANCE.reset();
    }

    @Override
    protected boolean shouldRunFile(File file) {
        GroovyLog.get().info(" - executing {}", file.toString());
        return true;
    }

    @Override
    protected void postRun() {
        if (!LOADER_PRE_INIT.equals(this.currentLoader)) {
            ReloadableRegistryManager.afterScriptRun();
        }
        MinecraftForge.EVENT_BUS.post(new ScriptRunEvent.Post());
        if (!LOADER_PRE_INIT.equals(this.currentLoader) && ReloadableRegistryManager.isFirstLoad()) {
            ReloadableRegistryManager.setLoaded();
        }
    }

    @Override
    public Iterable<File> getScriptFiles() {
        return GroovyScript.getRunConfig().getSortedFiles(currentLoader);
    }

    public static String relativizeSource(String source) {
        try {
            Path path = Paths.get(new URL(source).toURI());
            Path mainPath = new File(GroovyScript.getScriptPath()).toPath();
            return mainPath.relativize(path).toString();
        } catch (URISyntaxException | MalformedURLException e) {
            GroovyScript.LOGGER.error("Error parsing script source '{}'", source);
            // don't log to GroovyLog here since it will cause a StackOverflow
            return source;
        }
    }
}
