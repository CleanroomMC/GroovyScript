package com.cleanroommc.groovyscript.helper;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.Function;

@GroovyBlacklist
public class JsonHelper {

    public static final JsonObject EMPTY_JSON = new JsonObject();
    public static final JsonArray EMPTY_JSON_ARRAY = new JsonArray();
    public static final JsonParser jsonParser = new JsonParser();
    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static float getFloat(JsonObject json, float defaultValue, String... keys) {
        for (String key : keys) {
            if (json.has(key)) {
                JsonElement jsonElement = json.get(key);
                if (jsonElement.isJsonPrimitive()) {
                    return jsonElement.getAsFloat();
                }
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static int getInt(JsonObject json, int defaultValue, String... keys) {
        for (String key : keys) {
            if (json.has(key)) {
                JsonElement jsonElement = json.get(key);
                if (jsonElement.isJsonPrimitive()) {
                    return jsonElement.getAsInt();
                }
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static boolean getBoolean(JsonObject json, boolean defaultValue, String... keys) {
        for (String key : keys) {
            if (json.has(key)) {
                JsonElement jsonElement = json.get(key);
                if (jsonElement.isJsonPrimitive()) {
                    return jsonElement.getAsBoolean();
                }
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static String getString(JsonObject json, String defaultValue, String... keys) {
        for (String key : keys) {
            if (json.has(key)) {
                JsonElement jsonElement = json.get(key);
                if (jsonElement.isJsonPrimitive()) {
                    return jsonElement.getAsString();
                }
            }
        }
        return defaultValue;
    }

    public static <T> T getObject(JsonObject json, T defaultValue, Function<JsonObject, T> factory, String... keys) {
        for (String key : keys) {
            if (json.has(key)) {
                JsonElement jsonElement = json.get(key);
                if (jsonElement.isJsonObject()) {
                    return factory.apply(jsonElement.getAsJsonObject());
                }
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static <T> T getElement(JsonObject json, T defaultValue, Function<JsonElement, T> factory, String... keys) {
        for (String key : keys) {
            if (json.has(key)) {
                JsonElement jsonElement = json.get(key);
                return factory.apply(jsonElement);
            }
        }
        return defaultValue;
    }

    public static JsonObject getJsonObject(JsonObject json, String... keys) {
        return getJsonObject(json, EMPTY_JSON, keys);
    }

    public static JsonObject getJsonObject(JsonObject json, JsonObject defaultJson, String... keys) {
        for (String key : keys) {
            if (json.has(key)) {
                JsonElement element = json.get(key);
                if (element.isJsonObject()) {
                    return element.getAsJsonObject();
                }
            }
        }
        return defaultJson;
    }

    public static JsonArray getJsonArray(JsonObject json, String... keys) {
        return getJsonArray(json, EMPTY_JSON_ARRAY, keys);
    }

    public static JsonArray getJsonArray(JsonObject json, JsonArray defaultJson, String... keys) {
        for (String key : keys) {
            if (json.has(key)) {
                JsonElement element = json.get(key);
                if (element.isJsonArray()) {
                    return element.getAsJsonArray();
                }
            }
        }
        return defaultJson;
    }

    public static JsonElement loadJson(File file) {
        try {
            if (!file.isFile()) return null;
            Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            JsonElement json = jsonParser.parse(new JsonReader(reader));
            reader.close();
            return json;
        } catch (Exception e) {
            GroovyScript.LOGGER.error("Failed to read file on path {}", file, e);
        }
        return null;
    }

    public static boolean saveJson(File file, JsonElement element) {
        try {
            if (!file.getParentFile().isDirectory()) {
                if (!file.getParentFile().mkdirs()) {
                    GroovyScript.LOGGER.error("Failed to create file dirs on path {}", file);
                }
            }
            Writer writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), StandardCharsets.UTF_8);
            writer.write(gson.toJson(element));
            writer.close();
            return true;
        } catch (Exception e) {
            GroovyScript.LOGGER.error("Failed to save file on path {}", file, e);
        }
        return false;
    }
}
