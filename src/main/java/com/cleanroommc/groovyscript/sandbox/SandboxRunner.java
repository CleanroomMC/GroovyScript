package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.IGroovyEnvironmentRegister;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.event.GroovyEventManager;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.interception.InterceptionManager;
import com.cleanroommc.groovyscript.sandbox.interception.SandboxSecurityException;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.jetbrains.annotations.Nullable;
import org.kohsuke.groovy.sandbox.SandboxTransformer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class SandboxRunner {

    private static final Pattern packagePattern = Pattern.compile("[a-zA-Z_]+[a-zA-Z0-9_$]*(.[a-zA-Z_]+[a-zA-Z0-9_$]*)*");
    private static URL[] scriptEnvironment;

    private static boolean running = false;

    private static final Map<String, Object> BINDINGS = new Object2ObjectOpenHashMap<>();

    public static boolean isCurrentlyRunning() {
        return running;
    }

    public static void registerBinding(String name, Object obj) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(obj);
        BINDINGS.put(name, obj);
    }

    public static void init() {
        scriptEnvironment = new URL[1];
        try {
            scriptEnvironment[0] = new File(GroovyScript.scriptPath).toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        for (ModContainer modContainer : Loader.instance().getActiveModList()) {
            Object mod = modContainer.getMod();
            if (mod instanceof IGroovyEnvironmentRegister) {
                IGroovyEnvironmentRegister environmentRegister = (IGroovyEnvironmentRegister) mod;
                for (Class<?> clazz : environmentRegister.getBannedClasses()) {
                    InterceptionManager.INSTANCE.banClass(clazz);
                }
                for (String package0 : environmentRegister.getBannedPackages()) {
                    InterceptionManager.INSTANCE.banPackage(package0);
                }
                for (Map.Entry<Class<?>, Collection<String>> entry : environmentRegister.getBannedMethods().entrySet()) {
                    InterceptionManager.INSTANCE.banMethods(entry.getKey(), entry.getValue());
                }
            }
        }

        registerBinding("EventManager", GroovyEventManager.INSTANCE);
        registerBinding("eventManager", GroovyEventManager.INSTANCE);
        registerBinding("event_manager", GroovyEventManager.INSTANCE);

        registerBinding("mods", ModSupport.INSTANCE);
    }

    @Nullable
    public static Throwable run() {
        try {
            runScript();
            return null;
        } catch (IOException | ScriptException | ResourceException e) {
            GroovyLog.LOG.error("An Exception occurred trying to run groovy!");
            e.printStackTrace();
            return e;
        } catch (Exception e) {
            GroovyLog.LOG.exception(e);
            return e;
        }
    }

    public static void runScript() throws IOException, ScriptException, ResourceException, SandboxSecurityException {
        GroovyLog.LOG.info("Running scripts");
        // prepare script running
        MinecraftForge.EVENT_BUS.post(new ScriptRunEvent.Pre());
        if (!ReloadableRegistryManager.isFirstLoad()) {
            ReloadableRegistryManager.onReload();
            MinecraftForge.EVENT_BUS.post(new GroovyReloadEvent());
        }
        SimpleGroovyInterceptor.makeSureExists();
        GroovyEventManager.INSTANCE.reset();
        GroovyScript.LOGGER.info("Script environments: {}", Arrays.toString(scriptEnvironment));
        // initialise script engine
        GroovyScriptEngine engine = new GroovyScriptEngine(scriptEnvironment);
        CompilerConfiguration config = new CompilerConfiguration(CompilerConfiguration.DEFAULT);
        config.addCompilationCustomizers(new SandboxTransformer());
        engine.setConfig(config);
        Binding binding = new Binding(BINDINGS);

        // find and run scripts
        running = true;
        for (File file : getStartupFiles()) {
            GroovyLog.LOG.info(" - executing %s", file.toString());
            engine.run(file.toString(), binding);
        }
        running = false;
        ReloadableRegistryManager.afterScriptRun();
        MinecraftForge.EVENT_BUS.post(new ScriptRunEvent.Post());
        if (ReloadableRegistryManager.isFirstLoad()) {
            ReloadableRegistryManager.setLoaded();
        }
    }

    private static File[] getStartupFiles() throws IOException {
        Path path = GroovyScript.startupPath.toPath();
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        File[] files = GroovyScript.startupPath.listFiles();
        if (files == null || files.length == 0) {
            Files.createFile(new File(path + "/main.groovy").toPath());
            return new File[0];
        }
        Path mainPath = new File(GroovyScript.scriptPath).toPath();
        return Arrays.stream(files).map(file -> mainPath.relativize(file.toPath()).toFile()).toArray(File[]::new);
    }

    public static void logError(String msg, String sourceName, int lineNumber) throws SandboxSecurityException {
        throw new SandboxSecurityException("SandboxSecurityException: " + msg + " in script " + relativizeSource(sourceName) + " in line " + lineNumber);
    }

    public static String relativizeSource(String source) {
        try {
            Path path = Paths.get(new URL(source).toURI());
            Path mainPath = new File(GroovyScript.scriptPath).toPath();
            return mainPath.relativize(path).toString();
        } catch (URISyntaxException | MalformedURLException e) {
            GroovyLog.LOG.error("Error parsing script source '%s'", source);
            return source;
        }
    }

    public static <T> T runClosure(Closure<T> closure, Object... args) {
        try {
            SimpleGroovyInterceptor.makeSureExists();
            running = true;
            T t = closure.call(args);
            running = false;
            return t;
        } catch (Exception e) {
            running = false;
            GroovyScript.LOGGER.error("Caught an exception trying to run a closure:");
            e.printStackTrace();
        }
        return null;
    }
}
