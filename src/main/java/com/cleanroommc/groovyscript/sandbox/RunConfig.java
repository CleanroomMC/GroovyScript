package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.GroovyScriptConfig;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.JsonHelper;
import com.cleanroommc.groovyscript.packmode.Packmode;
import com.google.common.base.CaseFormat;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
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
        JsonObject packmode = new JsonObject();
        packmode.add("values", new JsonArray());
        packmode.addProperty("default", "");
        packmode.addProperty("_comment", "By default the packmode is not synced with the packmode mod. You can enable integration, but you can no longer change packmode on the fly.");
        packmode.addProperty("integratePackmodeMod", false);
        json.add("packmode", packmode);
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
    private final List<String> packmodeList = new ArrayList<>();
    private final Set<String> packmodeSet = new ObjectOpenHashSet<>();
    private final Map<String, List<String>> packmodePaths = new Object2ObjectOpenHashMap<>();
    private boolean integratePackmodeMod = false;
    // TODO asm
    private final String asmClass = null;
    private boolean debug;


    private final boolean invalidPackId;
    private boolean warnedAboutInvalidPackId = false;
    private int packmodeConfigState;

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
            name = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, id).replace('_', ' ');
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
    public void reload(JsonObject json, boolean init) {
        if (GroovyScript.isSandboxLoaded() && GroovyScript.getSandbox().isRunning()) {
            throw new RuntimeException();
        }
        this.debug = JsonHelper.getBoolean(json, false, "debug");
        this.classes.clear();
        this.loaderPaths.clear();
        this.packmodeList.clear();
        this.packmodeSet.clear();
        this.packmodePaths.clear();
        this.packmodeConfigState = 0;

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

        // packmode
        JsonObject jsonPackmode = JsonHelper.getJsonObject(json, "packmode");
        if (init) this.integratePackmodeMod = JsonHelper.getBoolean(jsonPackmode, false, "integratePackmodeMod");
        JsonArray modes = JsonHelper.getJsonArray(jsonPackmode, "values", "types");
        for (JsonElement je : modes) {
            if (je.isJsonPrimitive()) {
                String pm = Alias.autoConvertTo(je.getAsString(), CaseFormat.UPPER_CAMEL);
                if (!this.packmodeSet.contains(pm)) {
                    Alias alias = Alias.generateOf(pm, CaseFormat.UPPER_CAMEL);
                    this.packmodeList.add(alias.get(alias.size() - 1));
                    this.packmodeSet.addAll(alias);
                }
            }
        }
        if (this.integratePackmodeMod && arePackmodesConfigured()) {
            this.packmodeConfigState |= 2;
            GroovyLog.get().error("Integration with the packmode mod is enabled, but packmodes are also configured in GroovyScript,");
            GroovyLog.get().error("You should use the packmode mod to configure packmodes if integration is enabled,");
        }
        if (arePackmodesConfigured() && !Packmode.hasPackmode()) {
            String pm;
            if (!GroovyScriptConfig.packmode.isEmpty()) {
                pm = GroovyScriptConfig.packmode;
            } else {
                pm = JsonHelper.getString(jsonPackmode, null, "default");
                if (pm == null) {
                    if (!this.packmodeList.isEmpty()) {
                        pm = this.packmodeList.get(0);
                    }
                }
            }
            if (pm != null) Packmode.updatePackmode(pm);
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

    public boolean isValidPackmode(String packmode) {
        return this.packmodeSet.contains(packmode);
    }

    public boolean arePackmodesConfigured() {
        return !this.packmodeSet.isEmpty();
    }

    public List<String> getPackmodeList() {
        return Collections.unmodifiableList(packmodeList);
    }

    public ResourceLocation makeLoc(String name) {
        return new ResourceLocation(getPackId(), name);
    }

    @ApiStatus.Internal
    public void initPackmode() {
        if (this.integratePackmodeMod && !Loader.isModLoaded("packmode")) {
            this.integratePackmodeMod = false;
            this.packmodeConfigState |= 1;
            GroovyLog.get().error("Integration with the packmode mod is enabled, but the packmode mod is not installed.");
            GroovyLog.get().error("Please disable integration or install the mod,");
        }
    }

    public boolean isIntegratePackmodeMod() {
        return integratePackmodeMod;
    }

    public int getPackmodeConfigState() {
        return packmodeConfigState;
    }

    public boolean isLoaderConfigured(String loader) {
        List<String> path = this.classes.get(loader);
        if (path != null && !path.isEmpty()) return true;
        path = this.loaderPaths.get(loader);
        return path != null && !path.isEmpty();
    }

    public Collection<File> getClassFiles(File root, String loader) {
        List<String> paths = this.classes.get("all");
        paths = paths == null ? new ArrayList<>() : new ArrayList<>(paths);
        if (this.classes.containsKey(loader)) {
            paths.addAll(this.classes.get(loader));
        }
        return getSortedFilesOf(root, paths);
    }

    public Collection<File> getSortedFiles(File root, String loader) {
        List<String> paths = loaderPaths.get(loader);
        if (paths == null || paths.isEmpty()) return Collections.emptyList();
        return getSortedFilesOf(root, paths);
    }

    private Collection<File> getSortedFilesOf(File root, Collection<String> paths) {
        Object2IntLinkedOpenHashMap<File> files = new Object2IntLinkedOpenHashMap<>();
        String separator = getSeparator();

        for (String path : paths) {
            File rootFile = new File(root, path);
            if (!rootFile.exists()) {
                continue;
            }
            int pathSize = path.split(separator).length;
            try (Stream<Path> stream = Files.walk(rootFile.toPath())) {
                stream.filter(path1 -> isGroovyFile(path1.toString()))
                        .map(Path::toFile)
                        //.filter(Preprocessor::validatePreprocessors)
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
        return new ArrayList<>(files.keySet());
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
