package com.cleanroommc.groovyscript;

import com.cleanroommc.groovyscript.command.GSCommand;
import com.cleanroommc.groovyscript.command.RunScriptsCommand;
import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

@Mod(modid = GroovyScript.ID, name = GroovyScript.NAME, version = GroovyScript.VERSION)
@Mod.EventBusSubscriber(modid = GroovyScript.ID)
public class GroovyScript {

    public static final String ID = "groovyscript";
    public static final String NAME = "GroovyScript";
    public static final String VERSION = "1.0.0";

    public static final Logger LOGGER = LogManager.getLogger(ID);

    public static String scriptPath;
    public static File startupPath;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        scriptPath = Loader.instance().getConfigDir().toPath().getParent().toString() + "/scripts";
        startupPath = new File(scriptPath + "/startup");
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        try {
            runScript();
        } catch (ScriptException | ResourceException | IOException e) {
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public void onServerLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new GSCommand());
    }

    public static void runScript() throws IOException, ScriptException, ResourceException {
        LOGGER.info("Running scripts");
        URL[] urls = getURLs();
        LOGGER.info("URLs: {}", Arrays.toString(urls));
        GroovyScriptEngine engine = new GroovyScriptEngine(urls);
        CompilerConfiguration config = new CompilerConfiguration();
        Binding binding = new Binding();

        for (File file : getStartupFiles()) {
            LOGGER.info(" - executing {}", file.toString());
            engine.run(file.toString(), binding);
        }
    }

    private static File[] getStartupFiles() throws IOException {
        Path path = startupPath.toPath();
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        File[] files = startupPath.listFiles();
        if (files == null || files.length == 0) {
            Files.createFile(new File(path + "/main.groovy").toPath());
        }
        files = startupPath.listFiles();
        Path mainPath = new File(scriptPath).toPath();
        return Arrays.stream(files).map(file -> mainPath.relativize(file.toPath()).toFile()).toArray(File[]::new);
    }

    public static URL[] getURLs() throws MalformedURLException {
        return new URL[]{
                new File(scriptPath).toURI().toURL(),
                getBasePathFor(GroovyScript.class),
                getBasePathFor(Minecraft.class)
        };
    }

    public static URL getBasePathFor(Class<?> clazz) throws MalformedURLException {
        String className = clazz.getName().replace(".", "/") + ".class";
        ClassLoader loader = clazz.getClassLoader();
        String url = Objects.requireNonNull(loader.getResource(className)).toString();
        url = url.substring(0, url.indexOf(className));
        return new URL(url);
    }
}
