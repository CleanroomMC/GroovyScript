package com.cleanroommc.groovyscript;

import com.cleanroommc.groovyscript.api.IGroovyEnvironmentRegister;
import com.cleanroommc.groovyscript.brackets.BracketHandlerManager;
import com.cleanroommc.groovyscript.command.GSCommand;
import com.cleanroommc.groovyscript.compat.vanilla.VanillaModule;
import com.cleanroommc.groovyscript.event.Events;
import com.cleanroommc.groovyscript.network.NetworkHandler;
import com.cleanroommc.groovyscript.sandbox.GroovyDeobfuscationMapper;
import com.cleanroommc.groovyscript.sandbox.RunConfig;
import com.cleanroommc.groovyscript.sandbox.SandboxRunner;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;

@Mod(modid = GroovyScript.ID, name = GroovyScript.NAME, version = GroovyScript.VERSION)
@Mod.EventBusSubscriber(modid = GroovyScript.ID)
public class GroovyScript implements IGroovyEnvironmentRegister {

    public static final String ID = "groovyscript";
    public static final String NAME = "GroovyScript";
    public static final String VERSION = "1.0.0";

    public static final Logger LOGGER = LogManager.getLogger(ID);

    public static String scriptPath;
    public static File startupPath;

    private static RunConfig runConfig;
    public static final JsonParser jsonParser = new JsonParser();
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        NetworkHandler.init();
        GroovyDeobfuscationMapper.init();
        scriptPath = Loader.instance().getConfigDir().toPath().getParent().toString() + File.separator + "groovy";
        startupPath = new File(scriptPath + "/startup");
        runConfig = createRunConfig();
        SandboxRunner.init();
        Events.init();
        BracketHandlerManager.init();
        VanillaModule.initializeBinding();

        SandboxRunner.run("preInit");
    }

    @Mod.EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        SandboxRunner.run("postInit");
    }

    @Mod.EventHandler
    public void onServerLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new GSCommand());
    }

    @Override
    public Collection<String> getBannedPackages() {
        return Arrays.asList(
                "com.cleanroommc.groovyscript.api",
                "com.cleanroommc.groovyscript.command",
                "com.cleanroommc.groovyscript.core.mixin",
                "com.cleanroommc.groovyscript.registry",
                "com.cleanroommc.groovyscript.sandbox"
        );
    }

    public static RunConfig getRunConfig() {
        return runConfig;
    }

    private static RunConfig createRunConfig() {
        File runConfigFile = new File(scriptPath + File.separator + "runConfig.json");
        if (!Files.exists(runConfigFile.toPath())) {
            JsonObject json = RunConfig.createDefaultJson();
            saveJson(runConfigFile, json);
            return new RunConfig(json);
        }
        JsonElement element = loadJson(runConfigFile);
        if (element == null) return new RunConfig(new JsonObject());
        if (!element.isJsonObject()){
            LOGGER.error("runConfig.json must be a json object!");
            return new RunConfig(new JsonObject());
        }
        return new RunConfig(element.getAsJsonObject());
    }

    public static JsonElement loadJson(File file) {
        try {
            if (!file.isFile()) return null;
            Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            JsonElement json = jsonParser.parse(new JsonReader(reader));
            reader.close();
            return json;
        } catch (Exception e) {
            LOGGER.error("Failed to read file on path {}", file, e);
        }
        return null;
    }

    public static boolean saveJson(File file, JsonElement element) {
        try {
            if (!file.getParentFile().isDirectory()) {
                if (!file.getParentFile().mkdirs()) {
                    LOGGER.error("Failed to create file dirs on path {}", file);
                }
            }
            Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            writer.write(gson.toJson(element));
            writer.close();
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to save file on path {}", file, e);
        }
        return false;
    }
}
