package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.JsonHelper;
import com.google.common.base.CaseFormat;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ModMetadata;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RunConfig {

    public static JsonObject createDefaultJson() {
        JsonObject json = new JsonObject();
        json.addProperty("packName", "PlaceHolder name");
        json.addProperty("packId", "placeholdername");
        json.addProperty("version", "1.0.0");
        json.addProperty("debug", false);
        JsonObject classes = new JsonObject();
        JsonArray preInit = new JsonArray();
        classes.add("preInit", preInit);
        json.add("classes", classes);
        JsonObject loaders = new JsonObject();
        json.add("loaders", loaders);
        preInit = new JsonArray();
        loaders.add("preInit", preInit);
        preInit.add("preInit/");
        JsonArray postInit = new JsonArray();
        loaders.add("postInit", postInit);
        postInit.add("postInit/");
        return json;
    }

    public static final ModMetadata modMetadata = new ModMetadata();

    static {
        modMetadata.modId = "placeholder";
        modMetadata.name = "Placeholder";
        modMetadata.version = "0.0.0";
    }

    private final String packName;
    private final String packId;
    private final String version;
    private final Map<String, List<String>> classes = new Object2ObjectOpenHashMap<>();
    private final Map<String, List<String>> loaderPaths = new Object2ObjectOpenHashMap<>();
    // TODO pack modes
    private final Map<String, List<String>> packmodePaths = new Object2ObjectOpenHashMap<>();
    // TODO asm
    private final String asmClass = null;
    private boolean debug;

    private final boolean invalidPackId;
    private boolean warnedAboutInvalidPackId = false;

    public static final String[] GROOVY_SUFFIXES = {".groovy", ".gvy", ".gy", ".gsh"};

    public static boolean isGroovyFile(String path) {
        for (String suffix : GROOVY_SUFFIXES) {
            if (path.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    public RunConfig(JsonObject json) {
        String name = JsonHelper.getString(json, "", "packName", "name");
        String id = JsonHelper.getString(json, "", "packId", "id");
        Pattern idPattern = Pattern.compile("[a-z_]+");
        this.invalidPackId = id.isEmpty() || !idPattern.matcher(id).matches();
        if (name.isEmpty() && !this.invalidPackId) {
            name = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_UNDERSCORE, id).replace('_', ' ');
        }
        this.packName = name;
        this.packId = id;
        this.version = JsonHelper.getString(json, "1.0.0", "version", "ver");
        modMetadata.modId = this.packId;
        modMetadata.name = this.packName;
        modMetadata.version = this.version;
        modMetadata.parent = GroovyScript.ID;
    }

    @ApiStatus.Internal
    public void reload(JsonObject json) {
        if (GroovyScript.isSandboxLoaded() && GroovyScript.getSandbox().isRunning()) {
            throw new RuntimeException();
        }
        this.debug = JsonHelper.getBoolean(json, false, "debug");
        this.classes.clear();
        this.loaderPaths.clear();
        this.packmodePaths.clear();

        String regex = File.separatorChar == '\\' ? "/" : "\\\\";
        String replacement = getSeparator();
        if (json.has("classes")) {
            JsonElement jsonClasses = json.get("classes");

            if (jsonClasses.isJsonArray()) {
                List<String> classes = this.classes.computeIfAbsent("all", key -> new ArrayList<>());
                for (JsonElement element : jsonClasses.getAsJsonArray()) {
                    classes.add(sanitizePath(element.getAsString().replaceAll(regex, replacement)));
                }
            } else if (jsonClasses.isJsonObject()) {
                for (Map.Entry<String, JsonElement> entry : jsonClasses.getAsJsonObject().entrySet()) {
                    List<String> classes = this.classes.computeIfAbsent(entry.getKey(), key -> new ArrayList<>());
                    if (entry.getValue().isJsonPrimitive()) {
                        classes.add(sanitizePath(entry.getValue().getAsString().replaceAll(regex, replacement)));
                    } else if (entry.getValue().isJsonArray()) {
                        for (JsonElement element : entry.getValue().getAsJsonArray()) {
                            classes.add(sanitizePath(element.getAsString().replaceAll(regex, replacement)));
                        }
                    }
                    if (classes.isEmpty()) {
                        this.classes.remove(entry.getKey());
                    }
                }
            }
        }


        JsonObject jsonLoaders = JsonHelper.getJsonObject(json, "loaders");
        List<Pair<String, String>> pathsList = new ArrayList<>();

        GroovyLog.Msg errorMsg = GroovyLog.msg("Fatal while parsing runConfig.json").add("Files should NOT be ran in multiple loaders!").logToMc().fatal();

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
        if (errorMsg.getSubMessages().size() > 1) {
            errorMsg.post();
        }
    }

    public String getPackName() {
        return packName;
    }

    public String getPackId() {
        if (this.invalidPackId && !this.warnedAboutInvalidPackId) {
            GroovyLog.msg("Fatal error while trying to use the pack id")
                    .add("specified pack id is invalid or empty ('{}')", this.packId)
                    .add("pack id must only contain lower case letters and underscores")
                    .add("see https://groovyscript-docs.readthedocs.io/en/latest/getting_started/#run-config for more info")
                    .fatal()
                    .post();
            this.warnedAboutInvalidPackId = true;
        }
        return packId;
    }

    public String getPackOrModId() {
        return this.invalidPackId ? GroovyScript.ID : this.packId;
    }

    public String getPackOrModName() {
        return this.packName.isEmpty() ? GroovyScript.NAME : this.packName;
    }

    public boolean isValidPackId() {
        return !invalidPackId;
    }

    public String getVersion() {
        return version;
    }

    public boolean isDebug() {
        return debug;
    }

    public ResourceLocation makeLoc(String name) {
        return new ResourceLocation(getPackId(), name);
    }

    public Collection<File> getClassFiles(String loader) {
        List<String> paths = this.classes.get("all");
        paths = paths == null ? new ArrayList<>() : new ArrayList<>(paths);
        if (this.classes.containsKey(loader)) {
            paths.addAll(this.classes.get(loader));
        }
        return getSortedFilesOf(paths);
    }

    public Collection<File> getSortedFiles(String loader) {
        List<String> paths = loaderPaths.get(loader);
        if (paths == null || paths.isEmpty()) return Collections.emptyList();
        return getSortedFilesOf(paths);
    }

    private Collection<File> getSortedFilesOf(Collection<String> paths) {
        Object2IntLinkedOpenHashMap<File> files = new Object2IntLinkedOpenHashMap<>();
        String separator = getSeparator();

        for (String path : paths) {
            File rootFile = new File(GroovyScript.getScriptPath() + File.separator + path);
            if (!rootFile.exists()) {
                continue;
            }
            int pathSize = path.split(separator).length;
            try (Stream<Path> stream = Files.walk(rootFile.toPath())) {
                stream.filter(path1 -> isGroovyFile(path1.toString()))
                        .map(Path::toFile)
                        .filter(Preprocessor::validatePreprocessors)
                        .sorted(Comparator.comparing(File::getPath))
                        .forEach(file -> {
                            if (files.containsKey(file)) {
                                if (pathSize > files.getInt(file)) {
                                    files.put(file, pathSize);
                                }
                            } else {
                                files.put(file, pathSize);
                            }
                        });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Path mainPath = GroovyScript.getScriptFile().toPath();
        return files.keySet().stream().map(file -> mainPath.relativize(file.toPath()).toFile()).collect(Collectors.toList());
    }

    private static String sanitizePath(String path) {
        while (path.endsWith("/") || path.endsWith("\\")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
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
                if (!errorMsg.getSubMessages().contains(msg)) {
                    errorMsg.add(msg);
                }
                valid = false;
            }
        }
        if (!valid) {
            errorMsg.add("removing path '{}' from loader '{}'", path, loader);
        }
        return valid;
    }
}
