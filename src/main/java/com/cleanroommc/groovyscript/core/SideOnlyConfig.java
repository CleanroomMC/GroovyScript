package com.cleanroommc.groovyscript.core;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.sandbox.SandboxData;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.ApiStatus;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
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
    private static JsonObject generatedCfg;
    private static JsonObject generatedClient;
    private static JsonObject generatedServer;

    public static void clientOnly(String className, String member) {
        addAutoDetectedFailingMembers(generatedClient, className, Collections.singletonList(member));
    }

    public static void serverOnly(String className, String member) {
        addAutoDetectedFailingMembers(generatedServer, className, Collections.singletonList(member));
    }

    static void init() {
        generatedCfg = readFile(SandboxData.getSideOnlyGeneratedFile());
        if (generatedCfg == null) {
            createGeneratedConfig();
        } else {
            generatedClient = generatedCfg.getAsJsonObject("client");
            generatedServer = generatedCfg.getAsJsonObject("server");
        }
        readFile(new File(SandboxData.getScriptFile(), "sideOnly.json"));
    }

    private static void createGeneratedConfig() {
        generatedCfg = new JsonObject();
        generatedClient = new JsonObject();
        generatedServer = new JsonObject();
        generatedCfg.addProperty("ATTENTION", "This file is auto generated. Manual editing is not advised.");
        generatedCfg.add("common", new JsonObject());
        generatedCfg.add("client", generatedClient);
        generatedCfg.add("server", generatedServer);

        clientOnly("net.minecraftforge.common.config.Configuration", "setCategoryConfigEntryClass()");
        writeGeneratedConfig();
    }

    @ApiStatus.Internal
    public static void addAutoDetectedFailingMembers(Class<?> c, List<String> members) {
        // if the current side is server, it means its likely meant for client
        addAutoDetectedFailingMembers(FMLLaunchHandler.side().isServer() ? generatedClient : generatedServer, c.getName(), members);
    }

    private static void addAutoDetectedFailingMembers(JsonObject sideJson, String className, List<String> members) {
        JsonArray classJson = sideJson.getAsJsonArray(className);
        if (classJson == null) {
            classJson = new JsonArray();
            sideJson.add(className, classJson);
        }
        main:
        for (String member : members) {
            for (JsonElement e : classJson) {
                if (e.isJsonPrimitive() && e.getAsString().equals(member)) {
                    continue main;
                }
            }
            classJson.add(member);
        }
    }

    public static void writeGeneratedConfig() {
        File file = SandboxData.getSideOnlyGeneratedFile();
        try {
            if (file.exists()) {
                file.delete();
            } else {
                file.getParentFile().mkdirs();
            }

            if (!file.getParentFile().isDirectory()) {
                if (!file.getParentFile().mkdirs()) {
                    GroovyScriptCore.LOG.error("Failed to create file dirs on path {}", file);
                }
            }
            Writer writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8);
            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(generatedCfg));
            writer.close();
        } catch (Exception e) {
            GroovyScriptCore.LOG.error("Failed to save file on path {}", file, e);
        }
    }

    private static JsonObject readFile(File sideOnlyConfig) {
        JsonObject json;
        try {
            if (!sideOnlyConfig.isFile()) return null;
            Reader reader = new InputStreamReader(new FileInputStream(sideOnlyConfig), StandardCharsets.UTF_8);
            JsonElement jsonElement = new JsonParser().parse(new JsonReader(reader));
            reader.close();
            if (jsonElement instanceof JsonObject jsonObject) {
                json = jsonObject;
            } else {
                return null;
            }
        } catch (Exception e) {
            GroovyScript.LOGGER.error("Failed to read file on path {}", sideOnlyConfig, e);
            return null;
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
        return json;
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
