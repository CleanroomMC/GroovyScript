package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.IGroovyEnvironmentRegister;
import com.cleanroommc.groovyscript.compat.ModHandler;
import com.cleanroommc.groovyscript.event.GroovyEventManager;
import com.cleanroommc.groovyscript.event.IGroovyEventHandler;
import com.cleanroommc.groovyscript.helper.recipe.CraftingRecipeHelper;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.interception.InterceptionManager;
import com.cleanroommc.groovyscript.sandbox.interception.SandboxSecurityException;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.codehaus.groovy.control.CompilerConfiguration;
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
    }

    public static boolean run() {
        try {
            runScript();
            return true;
        } catch (IOException | ScriptException | ResourceException e) {
            GroovyLog.LOG.error("An Exception occurred trying to run groovy!");
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            GroovyLog.LOG.exception(e);
            return false;
        }
    }

    public static void runScript() throws IOException, ScriptException, ResourceException, SandboxSecurityException {
        GroovyLog.LOG.info("Running scripts");
        // prepare script running
        ReloadableRegistryManager.setShouldRegisterAsReloadable(true);
        MinecraftForge.EVENT_BUS.post(new ScriptRunEvent.Pre());
        GroovyEventManager.clearAllListeners();
        ReloadableRegistryManager.onReload();
        SimpleGroovyInterceptor.makeSureExists();

        GroovyScript.LOGGER.info("Script environments: {}", Arrays.toString(scriptEnvironment));
        // initialise script engine
        GroovyScriptEngine engine = new GroovyScriptEngine(scriptEnvironment);
        CompilerConfiguration config = new CompilerConfiguration(CompilerConfiguration.DEFAULT);
        config.addCompilationCustomizers(new SandboxTransformer());
        engine.setConfig(config);
        Binding binding = new Binding();
        binding.setVariable("events", (IGroovyEventHandler) () -> GroovyEventManager.MAIN);
        binding.setVariable("recipes", new CraftingRecipeHelper());
        binding.setVariable("mods", ModHandler.INSTANCE);

        // find and run scripts
        for (File file : getStartupFiles()) {
            GroovyLog.LOG.info(" - executing %s", file.toString());
            engine.run(file.toString(), binding);
        }
        MinecraftForge.EVENT_BUS.post(new ScriptRunEvent.Post());
        ReloadableRegistryManager.setShouldRegisterAsReloadable(false);
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

    public static Object runClosure(Closure<?> closure, Object... args) {
        try {
            SimpleGroovyInterceptor.makeSureExists();
            return closure.call(args);
        } catch (Exception e) {
            GroovyScript.LOGGER.error("Caught an exception trying to run a closure:");
            e.printStackTrace();
        }
        return null;
    }
}
