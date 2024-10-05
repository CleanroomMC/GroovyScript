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

public class SideOnlyConfig {

    private static final MethodSet CLASS_MARKER = new MethodSet(true);
    private static final Map<String, MethodSet> clientRemovals = new Object2ObjectOpenHashMap<>();
    private static final Map<String, MethodSet> serverRemovals = new Object2ObjectOpenHashMap<>();

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
        for (String key : new String[]{"common", "both", "all"}) {
            if (json.has(key)) {
                Map<String, MethodSet> commonRemovals = new Object2ObjectOpenHashMap<>();
                readConfig(commonRemovals, json.getAsJsonObject(key));
                for (var entry : commonRemovals.entrySet()) {
                    if (entry.getValue().bannsClass) {
                        clientRemovals.put(entry.getKey(), CLASS_MARKER);
                        serverRemovals.put(entry.getKey(), CLASS_MARKER);
                    } else {
                        clientRemovals.computeIfAbsent(entry.getKey(), k -> new MethodSet(false)).addAll(entry.getValue());
                        serverRemovals.computeIfAbsent(entry.getKey(), k -> new MethodSet(false)).addAll(entry.getValue());
                    }
                }
                break;
            }
        }
    }

    private static void readConfig(Map<String, MethodSet> removals, JsonObject json) {
        if (json.size() == 0) return;
        for (var entry : json.entrySet()) {
            if ("classes".equals(entry.getKey())) {
                for (JsonElement je : entry.getValue().getAsJsonArray()) {
                    removals.put(je.getAsString(), CLASS_MARKER);
                }
                continue;
            }
            MethodSet properties = removals.computeIfAbsent(entry.getKey(), k -> new MethodSet(false));
            if (properties.bannsClass) continue;
            if (entry.getValue().isJsonArray()) {
                for (JsonElement je : entry.getValue().getAsJsonArray()) {
                    properties.add(je.getAsString());
                }
            } else if (entry.getValue().isJsonPrimitive()) {
                properties.add(entry.getValue().getAsString());
            }
        }
    }

    public static MethodSet getRemovedProperties(Side side, String clazz) {
        // remove server removals on client side and client removals on server side
        return side.isClient() ? serverRemovals.get(clazz) : clientRemovals.get(clazz);
    }

    public static class MethodSet extends ObjectOpenHashSet<String> {

        public final boolean bannsClass;

        public MethodSet(boolean bannsClass) {
            this.bannsClass = bannsClass;
        }
    }
}
