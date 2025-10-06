package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.JsonHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.thread.SidedThreadGroup;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Stream;

/**
 * This class is intended to be loaded at core mod time, and makes sure no unnecessary classes are loaded.
 */
public class SandboxData {

    public static final String MIXIN_PKG = "mixins";

    private static final FileVisitOption[] FOLLOW_LINKS = {
            FileVisitOption.FOLLOW_LINKS
    };
    private static final FileVisitOption[] NO_VISIT_OPTIONS = {};

    public static final String[] GROOVY_SUFFIXES = {
            ".groovy", ".gvy", ".gy", ".gsh"
    };
    private static ActualSide physicalSide;
    private static final ThreadLocal<ActualSide> logicalSide = new ThreadLocal<>();
    private static File minecraftHome;
    private static File scriptPath;
    private static File mixinPath;
    private static File runConfigFile;
    private static File resourcesFile;
    private static File cacheBasePath;
    private static File standardScriptCachePath;
    private static File mixinScriptCachePath;
    private static URL[] rootUrls;
    private static URL[] mixinRootUrls;
    private static RunConfig runConfig;
    private static boolean initialised = false;
    private static boolean initialisedLate = false;

    private SandboxData() {}

    @ApiStatus.Internal
    public static void initialize(File minecraftHome, Logger log) {
        if (SandboxData.initialised) return;
        physicalSide = ActualSide.ofPhysicalSide(FMLLaunchHandler.side());

        try {
            SandboxData.minecraftHome = Objects.requireNonNull(minecraftHome, "Minecraft Home can't be null!").getCanonicalFile();
        } catch (IOException e) {
            GroovyLog.get().errorMC("Failed to canonicalize minecraft home path '" + minecraftHome + "'!");
            throw new RuntimeException(e);
        }
        cacheBasePath = new File(SandboxData.minecraftHome, "cache" + File.separator + "groovy");
        standardScriptCachePath = new File(cacheBasePath, "standard");
        mixinScriptCachePath = new File(cacheBasePath, "mixin");
        // If we are launching with the environment variable set to use the examples folder, use the examples folder for easy and consistent testing.
        if (Boolean.parseBoolean(System.getProperty("groovyscript.use_examples_folder"))) {
            scriptPath = new File(SandboxData.minecraftHome.getParentFile(), "examples");
        } else {
            scriptPath = new File(SandboxData.minecraftHome, "groovy");
        }
        try {
            scriptPath = scriptPath.getCanonicalFile();
        } catch (IOException e) {
            log.error("Failed to canonicalize groovy script path '{}'!", scriptPath);
            log.throwing(e);
        }
        mixinPath = new File(scriptPath, MIXIN_PKG);
        runConfigFile = new File(scriptPath, "runConfig.json");
        resourcesFile = new File(scriptPath, "assets");
        try {
            rootUrls = new URL[]{
                    scriptPath.toURI().toURL()
            };
            mixinRootUrls = new URL[]{
                    mixinPath.toURI().toURL()
            };
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Failed to create URL from script path " + scriptPath);
        }
        initialised = true;
        reloadRunConfig(true);
    }

    @ApiStatus.Internal
    public static void onLateInit() {
        if (initialisedLate) return;
        initialisedLate = true;
        physicalSide = ActualSide.ofPhysicalSide(FMLCommonHandler.instance().getSide());
    }

    public static @NotNull ActualSide getPhysicalSide() {
        ensureLoaded();
        return physicalSide;
    }

    public static ActualSide getLogicalSide() {
        ensureLoaded();
        if (!SandboxData.initialisedLate) return SandboxData.physicalSide;
        ActualSide side = logicalSide.get();
        if (side == null) {
            // try to obtain the effective side similar to FMLCommonHandler
            ThreadGroup group = Thread.currentThread().getThreadGroup();
            if (group instanceof SidedThreadGroup sidedThreadGroup) {
                // current thread is valid -> store it in thread local
                side = ActualSide.ofLogicalSide(sidedThreadGroup.getSide());
                logicalSide.set(side);
                return side;
            }
            // current thread is invalid to retrieve side -> fallback to physical side
            return SandboxData.physicalSide;
        }
        return side;
    }

    public static @NotNull String getScriptPath() {
        return getScriptFile().getPath();
    }

    public static @NotNull File getMinecraftHome() {
        ensureLoaded();
        return minecraftHome;
    }

    public static @NotNull File getScriptFile() {
        ensureLoaded();
        return scriptPath;
    }

    public static @NotNull File getMixinFile() {
        ensureLoaded();
        return mixinPath;
    }

    public static @NotNull File getResourcesFile() {
        ensureLoaded();
        return resourcesFile;
    }

    public static @NotNull File getRunConfigFile() {
        ensureLoaded();
        return runConfigFile;
    }

    public static @NotNull File getCacheBasePath() {
        ensureLoaded();
        return cacheBasePath;
    }

    public static @NotNull File getStandardScriptCachePath() {
        ensureLoaded();
        return standardScriptCachePath;
    }

    public static @NotNull File getMixinScriptCachePath() {
        ensureLoaded();
        return mixinScriptCachePath;
    }

    public static @NotNull URL getRootUrl() {
        return getRootUrls()[0];
    }

    public static @NotNull URL[] getRootUrls() {
        ensureLoaded();
        return rootUrls;
    }

    public static @NotNull URL getMixinRootUrl() {
        return getMixinRootUrls()[0];
    }

    public static @NotNull URL[] getMixinRootUrls() {
        ensureLoaded();
        return mixinRootUrls;
    }

    public static RunConfig getRunConfig() {
        ensureLoaded();
        return runConfig;
    }

    private static void ensureLoaded() {
        if (!initialised) {
            throw new IllegalStateException("Sandbox data is not yet Initialised.");
        }
    }

    public static boolean isInitialised() {
        return initialised;
    }

    public static boolean isInitialisedLate() {
        return initialisedLate;
    }

    public static File getRelativeFile(File file) {
        return new File(getRelativePath(file.getPath()));
    }

    public static String getRelativePath(String path) {
        return FileUtil.relativize(getScriptPath(), path);
    }

    @ApiStatus.Internal
    public static void reloadRunConfig(boolean init) {
        JsonElement element = JsonHelper.loadJson(getRunConfigFile());
        if (element == null || !element.isJsonObject()) element = new JsonObject();
        JsonObject json = element.getAsJsonObject();
        if (runConfig == null) {
            if (!Files.exists(getRunConfigFile().toPath())) {
                json = RunConfig.createDefaultJson();
                runConfig = createRunConfig(json);
            } else {
                runConfig = new RunConfig(json);
            }
        }
        runConfig.reload(json, init);
    }

    private static RunConfig createRunConfig(JsonObject json) {
        JsonHelper.saveJson(getRunConfigFile(), json);
        File main = new File(getScriptFile().getPath() + File.separator + "postInit" + File.separator + "main.groovy");
        if (!Files.exists(main.toPath())) {
            try {
                main.getParentFile().mkdirs();
                Files.write(main.toPath(), "\nlog.info('Hello World!')\n".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new RunConfig(json);
    }

    static Collection<File> getSortedFilesOf(File root, Collection<String> paths, boolean debug) {
        Object2IntLinkedOpenHashMap<File> files = new Object2IntLinkedOpenHashMap<>();
        String separator = File.separatorChar == '\\' ? "\\\\" : File.separator;

        for (String path : paths) {
            File rootFile = new File(root, path);
            if (!rootFile.exists()) {
                continue;
            }
            // if we are looking at a specific file, we don't want that to be overridden.
            // otherwise, we want to use the specificity based on the number of file separators.
            int pathSize = StringUtils.countMatches(path, separator);
            try (Stream<Path> stream = Files.walk(rootFile.toPath(), debug ? FOLLOW_LINKS : NO_VISIT_OPTIONS)) {
                stream.filter(path1 -> isGroovyFile(path1.toString()))
                        .map(Path::toFile)
                        //.filter(Preprocessor::validatePreprocessors)
                        .sorted(Comparator.comparing(File::getPath))
                        .forEach(file -> {
                            if (files.containsKey(file)) {
                                // if the file already exists, push the priority down if we are more specific than the already existing entry
                                if (pathSize > files.getInt(file)) {
                                    files.putAndMoveToLast(file, pathSize);
                                }
                            } else {
                                files.put(file, pathSize);
                            }
                        });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (files.isEmpty()) return Collections.emptyList();
        return new ArrayList<>(files.keySet());
    }

    public static boolean isGroovyFile(String path) {
        for (String suffix : GROOVY_SUFFIXES) {
            if (path.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }
}
