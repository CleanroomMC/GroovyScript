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
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModMetadata;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RunConfig {

    public static JsonObject createDefaultJson() {
        JsonObject json = new JsonObject();
        json.addProperty("packName", "PlaceHolder name");
        json.addProperty("packId", "placeholdername");
        json.addProperty("author", "Placeholder, author");
        json.addProperty("version", "1.0.0");
        json.addProperty("debug", false);
        JsonObject loaders = new JsonObject();
        json.add("loaders", loaders);
        JsonArray preInit = new JsonArray();
        loaders.add("preInit", preInit);
        preInit.add("classes/");
        preInit.add("preInit/");
        JsonArray postInit = new JsonArray();
        loaders.add("postInit", postInit);
        postInit.add("postInit/");
        JsonObject packmode = new JsonObject();
        packmode.add("values", new JsonArray());
        packmode.addProperty("default", "");
        packmode.addProperty(
                "_comment",
                "By default the packmode is not synced with the packmode mod. You can enable integration, but you can no longer change packmode on the fly.");
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
    private final List<String> packAuthors;
    private final Map<String, List<String>> loaderPaths = new Object2ObjectOpenHashMap<>();
    private final List<String> packmodeList = new ArrayList<>();
    private final Set<String> packmodeSet = new ObjectOpenHashSet<>();
    private final Map<String, List<String>> packmodePaths = new Object2ObjectOpenHashMap<>();
    private boolean integratePackmodeMod;
    // TODO asm
    private final String asmClass = null;
    private boolean debug;


    private final boolean invalidPackId;
    private boolean warnedAboutInvalidPackId;
    private int packmodeConfigState;

    public static final String[] GROOVY_SUFFIXES = SandboxData.GROOVY_SUFFIXES;

    public static boolean isGroovyFile(String path) {
        return SandboxData.isGroovyFile(path);
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
        JsonElement authors = null;
        if (json.has("author")) authors = json.get("author");
        else if (json.has("packAuthors")) authors = json.get("packAuthors");
        if (authors != null) {
            List<String> packAuthors = new ArrayList<>();
            if (authors.isJsonPrimitive()) {
                // author list in a single string separated by a comma
                packAuthors.addAll(
                        Arrays.stream(StringUtils.split(authors.getAsString(), ","))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .collect(Collectors.toList()));
            } else if (authors.isJsonArray()) {
                // authors in a json list, each entry is an author
                for (JsonElement author : authors.getAsJsonArray()) {
                    if (author.isJsonPrimitive()) {
                        packAuthors.add(author.getAsString());
                    }
                }
            }
            this.packAuthors = Collections.unmodifiableList(packAuthors);
        } else {
            this.packAuthors = Collections.emptyList();
        }
        modMetadata.modId = this.packId;
        modMetadata.name = this.packName;
        modMetadata.version = this.version;
        modMetadata.parent = GroovyScript.ID;
        modMetadata.authorList.addAll(this.packAuthors);
    }

    @ApiStatus.Internal
    public void reload(JsonObject json, boolean init) {
        if (GroovyScript.isSandboxLoaded() && GroovyScript.getSandbox().isRunning()) {
            throw new RuntimeException();
        }
        this.debug = JsonHelper.getBoolean(json, false, "debug");
        this.loaderPaths.clear();
        this.packmodeList.clear();
        this.packmodeSet.clear();
        this.packmodePaths.clear();
        this.packmodeConfigState = 0;

        String regex = File.separatorChar == '\\' ? "/" : "\\\\";
        String replacement = getSeparator();
        if (json.has("classes")) {
            throw new IllegalStateException("GroovyScript classes definition in runConfig is deprecated! Classes are now treated as normal scripts.");
        }

        JsonObject jsonLoaders = JsonHelper.getJsonObject(json, "loaders");
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
                    .add("see https://cleanroommc.com/groovy-script/getting_started/run_config for more info")
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

    public List<String> getPackAuthors() {
        return packAuthors;
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
        List<String> path = this.loaderPaths.get(loader);
        return path != null && !path.isEmpty();
    }

    public Collection<File> getSortedFiles(File root, String loader) {
        List<String> paths = loaderPaths.get(loader);
        if (paths == null || paths.isEmpty()) return Collections.emptyList();
        return SandboxData.getSortedFilesOf(root, paths, isDebug());
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
