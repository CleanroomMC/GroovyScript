package com.cleanroommc.groovyscript.core;

import com.cleanroommc.groovyscript.GroovyScript;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

public class SideOnlyConfig {

    private static final Map<String, Set<String>> clientRemovals = new Object2ObjectOpenHashMap<>();
    private static final Map<String, Set<String>> serverRemovals = new Object2ObjectOpenHashMap<>();

    static void init() {

        initConfig((File) FMLInjectionData.data()[6]);
    }

    private static void initConfig(File minecraftHome) {
        File scriptPath;
        if (Boolean.parseBoolean(System.getProperty("groovyscript.use_examples_folder"))) {
            scriptPath = new File(minecraftHome.getParentFile(), "examples");
        } else {
            scriptPath = new File(minecraftHome, "groovy");
        }
        File sideOnlyConfig = new File(scriptPath, "sideOnly.json");
        JsonObject json;
        try {
            if (!sideOnlyConfig.isFile()) return;
            Reader reader = new InputStreamReader(new FileInputStream(sideOnlyConfig), StandardCharsets.UTF_8);
            JsonElement jsonElement = new JsonParser().parse(new JsonReader(reader));
            reader.close();
            if (jsonElement instanceof JsonObject jsonObject) {
                json = jsonObject;
            } else {
                return;
            }
        } catch (Exception e) {
            GroovyScript.LOGGER.error("Failed to read file on path {}", sideOnlyConfig, e);
            return;
        }
        if (json.has("client")) {
            readConfig(clientRemovals, json.getAsJsonObject("client"));
        }
        if (json.has("server")) {
            readConfig(serverRemovals, json.getAsJsonObject("server"));
        }
    }

    private static void readConfig(Map<String, Set<String>> removals, JsonObject json) {
        if (json.size() == 0) return;
        for (var entry : json.entrySet()) {
            Set<String> properties = removals.computeIfAbsent(entry.getKey(), k -> new ObjectOpenHashSet<>());
            if (entry.getValue().isJsonArray()) {
                for (JsonElement je : entry.getValue().getAsJsonArray()) {
                    properties.add(je.getAsString());
                }
            } else if(entry.getValue().isJsonPrimitive()) {
                properties.add(entry.getValue().getAsString());
            }
        }
    }

    public static Set<String> getRemovedProperties(Side side, String clazz) {
        // remove server removals on client side and client removals on server side
        return side.isClient() ? serverRemovals.get(clazz) : clientRemovals.get(clazz);
    }
}
