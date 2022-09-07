package com.cleanroommc.groovyscript.sandbox;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RunConfig {

    private final String packName;
    private final String version;
    private final Map<String, List<String>> loaderPaths = new Object2ObjectOpenHashMap<>();
    // TODO pack modes
    private final Map<String, List<String>> packmodePaths = new Object2ObjectOpenHashMap<>();
    // TODO asm
    private final String asmClass = null;

    public RunConfig(JsonObject json) {
        this.packName = json.get("packName").getAsString();
        this.version = json.get("version").getAsString();
        JsonObject jsonLoaders = json.getAsJsonObject("loaders");
        for (Map.Entry<String, JsonElement> entry : jsonLoaders.entrySet()) {
            JsonArray loader = (JsonArray) entry.getValue();
            List<String> paths = new ArrayList<>();
            for (JsonElement element : loader) {
                paths.add(element.getAsString());
            }
            loaderPaths.put(entry.getKey(), paths);
        }
    }

    public String getPackName() {
        return packName;
    }

    public String getVersion() {
        return version;
    }
}
