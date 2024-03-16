package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.packmode.Packmode;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.google.common.base.CaseFormat;
import io.sommers.packmode.api.PackModeAPI;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.BiPredicate;

public class Preprocessor {

    private static final Object2ObjectArrayMap<String, BiPredicate<File, String[]>> PREPROCESSORS = new Object2ObjectArrayMap<>();
    private static final String[] NO_ARGS = new String[0];

    public static void registerPreprocessor(String name, BiPredicate<File, String[]> test) {
        PREPROCESSORS.put(name.toUpperCase(Locale.ROOT), test);
    }

    static {
        registerPreprocessor("NO_RUN", (file, args) -> false);
        registerPreprocessor("DEBUG_ONLY", (file, args) -> GroovyScript.getRunConfig().isDebug());
        registerPreprocessor("NO_RELOAD", (file, args) -> !ReloadableRegistryManager.isFirstLoad());
        registerPreprocessor("MODS_LOADED", Preprocessor::checkModsLoaded);
        registerPreprocessor("SIDE", Preprocessor::checkSide);
        registerPreprocessor("PACKMODE", Preprocessor::checkPackmode);
    }

    public static List<String> parsePreprocessors(File file) {
        List<String> preprocessors = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            boolean isComment = false;
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.startsWith("/*")) {
                    isComment = true;
                    line = line.substring(2).trim();
                    if (line.isEmpty()) continue;
                }
                if (line.startsWith("//")) {
                    line = line.substring(2).trim();
                    if (line.isEmpty()) continue;
                } else if (!isComment) {
                    return preprocessors.isEmpty() ? Collections.emptyList() : preprocessors;
                }
                if (isComment && line.endsWith("*/")) {
                    isComment = false;
                }

                if (isPreprocessor(line)) {
                    preprocessors.add(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return preprocessors.isEmpty() ? Collections.emptyList() : preprocessors;
    }

    public static boolean validatePreprocessor(File file, List<String> preprocessors) {
        for (String pp : preprocessors) {
            if (!processPreprocessor(file, pp)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isPreprocessor(String line) {
        String s = line.split(":", 2)[0];
        return PREPROCESSORS.containsKey(s.toUpperCase(Locale.ROOT));
    }

    private static boolean processPreprocessor(File file, String line) {
        String[] parts = line.split(":", 2);
        String[] args = NO_ARGS;
        if (parts.length > 1) {
            args = parts[1].split(",");
            for (int i = 0; i < args.length; i++) {
                args[i] = args[i].trim();
            }
        }
        String s = parts[0];
        BiPredicate<File, String[]> preprocessor = PREPROCESSORS.get(s.toUpperCase(Locale.ROOT));
        return preprocessor.test(file, args);
    }

    private static boolean checkModsLoaded(File file, String[] mods) {
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
        if ("CLIENT".equals(side)) {
            return FMLCommonHandler.instance().getSide().isClient();
        }
        if ("SERVER".equals(side)) {
            return FMLCommonHandler.instance().getSide().isServer();
        }
        GroovyLog.get().error("Side processor argument in file '{}' must be CLIENT or SERVER (lower case is allowed too)", file.getName());
        return true;
    }

    private static boolean checkPackmode(File file, String[] modes) {
        for (String mode : modes) {
            if (!Packmode.isValidPackmode(mode)) {
                List<String> valid = GroovyScript.getRunConfig().isIntegratePackmodeMod() ? PackModeAPI.getInstance().getPackModes()
                                                                                          : GroovyScript.getRunConfig().getPackmodeList();
                GroovyLog.get().error("The packmode '{}' specified in file '{}' does not exist. Valid values are {}", mode, file.getName(), valid);
            } else if (Packmode.getPackmode().equals(Alias.autoConvertTo(mode, CaseFormat.LOWER_UNDERSCORE))) {
                return true;
            }
        }
        return false;
    }
}
