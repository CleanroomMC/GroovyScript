package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.PairList;
import com.cleanroommc.groovyscript.packmode.Packmode;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.google.common.base.CaseFormat;
import io.sommers.packmode.api.PackModeAPI;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.BiPredicate;

public class Preprocessor {

    private static final Object2ObjectArrayMap<String, BiPredicate<File, String[]>> PREPROCESSORS = new Object2ObjectArrayMap<>();
    private static final String[] NO_ARGS = new String[0];
    public static final String NO_RUN = "NO_RUN";
    public static final String DEBUG_ONLY = "DEBUG_ONLY";
    public static final String NO_RELOAD = "NO_RELOAD";
    public static final String MODS_LOADED = "MODS_LOADED";
    public static final String SIDE = "SIDE";
    public static final String PACKMODE = "PACKMODE";

    public static final String SIDE_CLIENT = "CLIENT";
    public static final String SIDE_SERVER = "SERVER";

    public static void registerPreprocessor(String name, BiPredicate<File, String[]> test) {
        PREPROCESSORS.put(name.toUpperCase(Locale.ROOT), test);
    }

    static {
        registerPreprocessor(NO_RUN, (file, args) -> false);
        registerPreprocessor(DEBUG_ONLY, (file, args) -> GroovyScript.getRunConfig().isDebug());
        registerPreprocessor(NO_RELOAD, (file, args) -> !ReloadableRegistryManager.isFirstLoad());
        registerPreprocessor(MODS_LOADED, Preprocessor::checkModsLoaded);
        registerPreprocessor(SIDE, Preprocessor::checkSide);
        registerPreprocessor(PACKMODE, Preprocessor::checkPackmode);
    }

    public static PairList<String, String> parsePreprocessors(File file) {
        PairList<String, String> preprocessors = new PairList<>();
        parsePreprocessors(file, preprocessors);
        return preprocessors;
    }

    public static int getImportStartLine(File file) {
        return parsePreprocessors(file, new PairList<>());
    }

    private static int parsePreprocessors(File file, PairList<String, String> preprocessors) {
        int lines = 0;
        int empty = 0;
        boolean lastEmpty = false;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            boolean isComment = false;
            String line;
            while ((line = br.readLine()) != null) {
                lines++;
                line = line.trim();
                if (!lastEmpty) empty = 0;
                if (line.isEmpty()) {
                    empty++;
                    lastEmpty = true;
                    continue;
                }
                lastEmpty = false;
                if (line.startsWith("/*")) {
                    if (line.endsWith("*/")) {
                        line = line.substring(2, line.length() - 2).trim();
                    } else if (line.contains("*/")) {
                        return preprocessors.isEmpty() ? 0 : lines - empty - 1;
                    } else {
                        isComment = true;
                        line = line.substring(2).trim();
                    }
                    if (line.isEmpty()) continue;
                }
                if (line.startsWith("//")) {
                    line = line.substring(2).trim();
                    if (line.isEmpty()) continue;
                } else if (!isComment) {
                    return preprocessors.isEmpty() ? 0 : lines - empty - 1;
                }
                if (isComment && line.endsWith("*/")) {
                    isComment = false;
                }

                String preprocessorName = line;
                String args = null;
                int i = line.indexOf(':');
                if (i > 0) {
                    preprocessorName = line.substring(0, i);
                    args = line.substring(i + 1);
                }
                preprocessorName = preprocessorName.trim().toUpperCase(Locale.ENGLISH);
                if (PREPROCESSORS.containsKey(preprocessorName)) {
                    preprocessors.add(preprocessorName, args);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return preprocessors.isEmpty() ? 0 : lines - empty - 1;
    }

    public static boolean containsPreProcessor(String ppName, PairList<String, String> preprocessors) {
        if (preprocessors == null || preprocessors.isEmpty()) return false;
        for (String name : preprocessors.getLeftIterable()) {
            if (ppName.equals(name)) return true;
        }
        return false;
    }

    public static boolean validatePreprocessor(File file, PairList<String, String> preprocessors) {
        for (Pair<String, String> pp : preprocessors) {
            if (!processPreprocessor(file, pp.getLeft(), pp.getRight())) {
                return false;
            }
        }
        return true;
    }

    private static boolean isPreprocessor(String line) {
        String s = line.split(":", 2)[0];
        return PREPROCESSORS.containsKey(s.toUpperCase(Locale.ROOT));
    }

    private static boolean processPreprocessor(File file, String name, String rawArgs) {
        String[] args = NO_ARGS;
        if (rawArgs != null && !rawArgs.isEmpty()) {
            args = rawArgs.split(",");
            for (int i = 0; i < args.length; i++) {
                args[i] = args[i].trim();
            }
        }
        BiPredicate<File, String[]> preprocessor = PREPROCESSORS.get(name);
        if (preprocessor == null) {
            throw new NullPointerException("Preprocessor '" + name + "' was previously valid, but was now not found!");
        }
        return preprocessor.test(file, args);
    }

    private static boolean checkModsLoaded(File file, String[] mods) {
        if (!SandboxData.isInitialisedLate()) {
            // mod data is not yet loaded
            // silently fail preprocessor
            return false;
        }
        for (String mod : mods) {
            if (!Loader.isModLoaded(mod)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkSide(File file, String[] sides) {
        if (sides.length != 1) {
            GroovyLog.get().error("Side preprocessor in file '{}' should have exactly one argument, but found {}", file.getName(), Arrays.asList(sides));
            return true;
        }
        String side = sides[0].toUpperCase();
        if (SIDE_CLIENT.equals(side)) {
            return SandboxData.getPhysicalSide().isClient();
        }
        if (SIDE_SERVER.equals(side)) {
            return SandboxData.getPhysicalSide().isServer();
        }
        GroovyLog.get().error("Side processor argument in file '{}' must be CLIENT or SERVER (lower case is allowed too)", file.getName());
        return true;
    }

    private static boolean checkPackmode(File file, String[] modes) {
        for (String mode : modes) {
            if (!Packmode.isValidPackmode(mode)) {
                List<String> valid = GroovyScript.getRunConfig().isIntegratePackmodeMod()
                                     ? PackModeAPI.getInstance().getPackModes()
                                     : GroovyScript.getRunConfig().getPackmodeList();
                GroovyLog.get().error("The packmode '{}' specified in file '{}' does not exist. Valid values are {}", mode, file.getName(), valid);
            } else if (Packmode.getPackmode().equals(Alias.autoConvertTo(mode, CaseFormat.LOWER_UNDERSCORE))) {
                return true;
            }
        }
        return false;
    }
}
