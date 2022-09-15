package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.helper.JsonHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class RunConfig {

    public static JsonObject createDefaultJson() {
        JsonObject json = new JsonObject();
        json.addProperty("packName", "");
        json.addProperty("version", "1.0.0");
        JsonObject loaders = new JsonObject();
        json.add("loaders", loaders);
        JsonArray preInit = new JsonArray();
        loaders.add("preInit", preInit);
        preInit.add("preInit/");
        JsonArray postInit = new JsonArray();
        loaders.add("postInit", postInit);
        postInit.add("postInit/");
        return json;
    }

    private final String packName;
    private final String version;
    private final Map<String, List<String>> loaderPaths = new Object2ObjectOpenHashMap<>();
    // TODO pack modes
    private final Map<String, List<String>> packmodePaths = new Object2ObjectOpenHashMap<>();
    // TODO asm
    private final String asmClass = null;

    private static final String GROOVY_SUFFIX = ".groovy";

    public RunConfig(JsonObject json) {
        this.packName = JsonHelper.getString(json, "", "packName", "name");
        this.version = JsonHelper.getString(json, "1.0.0", "version", "ver");

        JsonObject jsonLoaders = JsonHelper.getJsonObject(json, "loaders");
        String regex = File.separatorChar == '\\' ? "/" : "\\\\";
        String replacement = getSeparator();
        List<Pair<String, String>> pathsList = new ArrayList<>();

        GroovyLog.Msg errorMsg = GroovyLog.msg("Fatal while parsing runConfig.json")
                .add("Files should NOT be ran in multiple loaders!")
                .logToMc()
                .fatal();

        for (Map.Entry<String, JsonElement> entry : jsonLoaders.entrySet()) {
            JsonArray loader = (JsonArray) entry.getValue();
            List<String> paths = new ArrayList<>();

            for (JsonElement element : loader) {
                String path = element.getAsString().replaceAll(regex, replacement);
                while (path.endsWith("/") || path.endsWith("\\")) {
                    path = path.substring(0, path.length() - 1);
                }
                if (!checkValid(errorMsg, pathsList, entry.getKey(), path)) continue;
                paths.add(path);
            }

            loaderPaths.put(entry.getKey(), paths);
            pathsList.addAll(paths.stream().map(path -> Pair.of(entry.getKey(), path)).collect(Collectors.toList()));
        }
        if (errorMsg.getMessageAmount() > 2) {
            errorMsg.post();
        }
    }

    public String getPackName() {
        return packName;
    }

    public String getVersion() {
        return version;
    }

    public Collection<File> getSortedFiles(String loader) {
        List<String> paths = loaderPaths.get(loader);
        if (paths == null || paths.isEmpty()) return Collections.emptyList();

        Object2IntLinkedOpenHashMap<File> files = new Object2IntLinkedOpenHashMap<>();
        String separator = getSeparator();

        for (String path : paths) {
            File[] listedFiles = new File(GroovyScript.getScriptPath() + File.separator + path).listFiles();
            if (listedFiles == null || listedFiles.length == 0) continue;
            int pathSize = path.split(separator).length;
            for (File file : listedFiles) {
                if (!file.getPath().endsWith(GROOVY_SUFFIX))
                    continue;
                if (files.containsKey(file)) {
                    if (pathSize > files.getInt(file)) {
                        files.put(file, pathSize);
                    }
                } else {
                    files.put(file, pathSize);
                }
            }
        }

        GroovyScript.LOGGER.info("Files: {}", files);
        Path mainPath = new File(GroovyScript.getScriptPath()).toPath();
        return files.keySet().stream().map(file -> mainPath.relativize(file.toPath()).toFile()).collect(Collectors.toList());
    }

    private static String getSeparator() {
        return File.separatorChar == '\\' ? "\\\\" : File.separator;
    }

    private static boolean checkValid(GroovyLog.Msg errorMsg, List<Pair<String, String>> paths, String loader, String path) {
        boolean valid = true;
        for (Pair<String, String> path1 : paths) {
            if (path1.getValue().startsWith(path) || path.startsWith(path1.getValue())) {
                String longPath = path;
                if (path1.getValue().length() > path.length()) longPath = path1.getValue();
                String msg = String.format("files in '%s' are configured for multiple loaders: '%s' and '%s'", longPath, loader, path1.getKey());
                if (!errorMsg.contains(msg)) {
                    errorMsg.add(msg);
                }
                valid = false;
            }
        }
        if (!valid) {
            errorMsg.add("removing path '%s' from loader '%s'", path, loader);
        }
        return valid;
    }
}
