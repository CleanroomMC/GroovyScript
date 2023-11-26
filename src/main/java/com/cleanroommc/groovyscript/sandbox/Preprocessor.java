package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.packmode.Packmode;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.google.common.base.CaseFormat;
import io.sommers.packmode.api.PackModeAPI;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

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

    public static boolean validatePreprocessors(File file) {
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
                    return true;
                }
                if (isComment && line.endsWith("*/")) {
                    isComment = false;
                }

                if (!processPreprocessor(file, line)) {
                    return false;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
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
        for (ObjectIterator<Object2ObjectMap.Entry<String, BiPredicate<File, String[]>>> iterator = PREPROCESSORS.object2ObjectEntrySet().fastIterator(); iterator.hasNext(); ) {
            Object2ObjectMap.Entry<String, BiPredicate<File, String[]>> entry = iterator.next();
            if (s.equalsIgnoreCase(entry.getKey())) {
                return entry.getValue().test(file, args);
            }
        }
        return true;
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
        if (side.equals("CLIENT")) {
            return FMLCommonHandler.instance().getSide().isClient();
        }
        if (side.equals("SERVER")) {
            return FMLCommonHandler.instance().getSide().isServer();
        }
        GroovyLog.get().error("Side processor argument in file '{}' must be CLIENT or SERVER (lower case is allowed too)", file.getName());
        return true;
    }

    private static boolean checkPackmode(File file, String[] modes) {
        for (String mode : modes) {
            if (!Packmode.isValidPackmode(mode)) {
                List<String> valid = GroovyScript.getRunConfig().isIntegratePackmodeMod() ? PackModeAPI.getInstance().getPackModes() : GroovyScript.getRunConfig().getPackmodeList();
                GroovyLog.get().error("The packmode '{}' specified in file '{}' does not exist. Valid values are {}", mode, file.getName(), valid);
            } else if (Packmode.getPackmode().equals(Alias.autoConvertTo(mode, CaseFormat.LOWER_UNDERSCORE))) {
                return true;
            }
        }
        return false;
    }
}
