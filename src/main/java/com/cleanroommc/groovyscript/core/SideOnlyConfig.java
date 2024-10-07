package com.cleanroommc.groovyscript.core;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.sandbox.SandboxData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

public class SideOnlyConfig {

    private static final MethodSet CLASS_MARKER = new MethodSet(true);
    private static final Function<String, MethodSet> DEFAULT_METHOD_SET = s -> new MethodSet(false);
    private static final String[] commonKeys = {
            "common", "both", "all"
    };
    private static final Map<String, MethodSet> clientRemovals = new Object2ObjectOpenHashMap<>();
    private static final Map<String, MethodSet> serverRemovals = new Object2ObjectOpenHashMap<>();

    public static void clientOnly(String className, String member) {
        serverRemovals.computeIfAbsent(className, DEFAULT_METHOD_SET).add(member);
    }

    public static void serverOnly(String className, String member) {
        clientRemovals.computeIfAbsent(className, DEFAULT_METHOD_SET).add(member);
    }

    static void init() {
        clientOnly("net.minecraftforge.common.config.Configuration", "setCategoryConfigEntryClass()");

        initConfig();
    }

    private static void initConfig() {
        File sideOnlyConfig = new File(SandboxData.getScriptFile(), "sideOnly.json");
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
            readConfig(serverRemovals, json.getAsJsonObject("client"));
        }
        if (json.has("server")) {
            readConfig(clientRemovals, json.getAsJsonObject("server"));
        }
        for (String key : commonKeys) {
            if (json.has(key)) {
                Map<String, MethodSet> commonRemovals = new Object2ObjectOpenHashMap<>();
                readConfig(commonRemovals, json.getAsJsonObject(key));
                for (var entry : commonRemovals.entrySet()) {
                    if (entry.getValue().bansClass) {
                        clientRemovals.put(entry.getKey(), CLASS_MARKER);
                        serverRemovals.put(entry.getKey(), CLASS_MARKER);
                    } else {
                        clientRemovals.computeIfAbsent(entry.getKey(), DEFAULT_METHOD_SET).addAll(entry.getValue());
                        serverRemovals.computeIfAbsent(entry.getKey(), DEFAULT_METHOD_SET).addAll(entry.getValue());
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
            MethodSet properties = removals.computeIfAbsent(entry.getKey(), DEFAULT_METHOD_SET);
            if (properties.bansClass) continue;
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
        return side.isClient() ? clientRemovals.get(clazz) : serverRemovals.get(clazz);
    }

    public static class MethodSet extends ObjectOpenHashSet<String> {

        public final boolean bansClass;

        public MethodSet(boolean bansClass) {
            this.bansClass = bansClass;
        }
    }
}
