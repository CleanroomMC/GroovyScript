package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.api.GroovyLog;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * This class is intended to be loaded at core mod time, and makes sure no unnecessary classes are loaded.
 */
public class SandboxData {

    private static final FileVisitOption[] FOLLOW_LINKS = {
            FileVisitOption.FOLLOW_LINKS
    };
    private static final FileVisitOption[] NO_VISIT_OPTIONS = {};

    public static final String[] GROOVY_SUFFIXES = {
            ".groovy", ".gvy", ".gy", ".gsh"
    };
    private static File minecraftHome;
    private static File scriptPath;
    private static File runConfigFile;
    private static File resourcesFile;
    private static File cachePath;
    private static URL rootUrl;
    private static URL[] rootUrls;
    private static boolean initialised = false;

    private SandboxData() {}

    @ApiStatus.Internal
    public static void initialize(File minecraftHome, Logger log) {
        if (SandboxData.initialised) return;

        try {
            SandboxData.minecraftHome = Objects.requireNonNull(minecraftHome, "Minecraft Home can't be null!").getCanonicalFile();
        } catch (IOException e) {
            GroovyLog.get().errorMC("Failed to canonicalize minecraft home path '" + minecraftHome + "'!");
            throw new RuntimeException(e);
        }
        cachePath = new File(SandboxData.minecraftHome, "cache" + File.separator + "groovy");
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
        runConfigFile = new File(scriptPath, "runConfig.json");
        resourcesFile = new File(scriptPath, "assets");
        try {
            rootUrl = scriptPath.toURI().toURL();
            rootUrls = new URL[]{
                    rootUrl
            };
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Failed to create URL from script path " + scriptPath);
        }
        initialised = true;
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

    public static @NotNull File getResourcesFile() {
        ensureLoaded();
        return resourcesFile;
    }

    public static @NotNull File getRunConfigFile() {
        ensureLoaded();
        return runConfigFile;
    }

    public static @NotNull File getCachePath() {
        ensureLoaded();
        return cachePath;
    }

    public static @NotNull URL getRootUrl() {
        ensureLoaded();
        return rootUrl;
    }

    public static @NotNull URL[] getRootUrls() {
        ensureLoaded();
        return rootUrls;
    }

    private static void ensureLoaded() {
        if (!initialised) {
            throw new IllegalStateException("Sandbox data is not yet Initialised.");
        }
    }

    public static boolean isInitialised() {
        return initialised;
    }

    public static File getRelativeFile(File file) {
        return new File(getRelativePath(file.getPath()));
    }

    public static String getRelativePath(String path) {
        return FileUtil.relativize(getScriptPath(), path);
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
