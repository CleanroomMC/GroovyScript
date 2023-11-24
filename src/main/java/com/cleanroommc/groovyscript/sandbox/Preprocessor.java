package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.Packmode;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.google.common.base.CaseFormat;
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
import java.util.Locale;
import java.util.function.Predicate;

public class Preprocessor {

    private static final Object2ObjectArrayMap<String, Predicate<String[]>> PREPROCESSORS = new Object2ObjectArrayMap<>();
    private static final String[] NO_ARGS = new String[0];

    public static void registerPreprocessor(String name, Predicate<String[]> test) {
        PREPROCESSORS.put(name.toUpperCase(Locale.ROOT), test);
    }

    static {
        registerPreprocessor("NO_RUN", args -> false);
        registerPreprocessor("DEBUG_ONLY", args -> GroovyScript.getRunConfig().isDebug());
        registerPreprocessor("NO_RELOAD", args -> !ReloadableRegistryManager.isFirstLoad());
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

                if (!processPreprocessor(line)) {
                    return false;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private static boolean processPreprocessor(String line) {
        String[] parts = line.split(":", 2);
        String[] args = NO_ARGS;
        if (parts.length > 1) {
            args = parts[1].split(",");
            for (int i = 0; i < args.length; i++) {
                args[i] = args[i].trim();
            }
        }
        String s = parts[0];
        for (ObjectIterator<Object2ObjectMap.Entry<String, Predicate<String[]>>> iterator = PREPROCESSORS.object2ObjectEntrySet().fastIterator(); iterator.hasNext(); ) {
            Object2ObjectMap.Entry<String, Predicate<String[]>> entry = iterator.next();
            if (s.equalsIgnoreCase(entry.getKey())) {
                return entry.getValue().test(args);
            }
        }
        return true;
    }

    private static boolean checkModsLoaded(String[] mods) {
        for (String mod : mods) {
            if (!Loader.isModLoaded(mod)) {
                return false;
            }
        }
        return true;
    }

    private static boolean checkSide(String[] sides) {
        if (sides.length != 1) {
            GroovyLog.get().error("Side preprocessor should have exactly one argument, but found {}", Arrays.asList(sides));
            return true;
        }
        String side = sides[0].toUpperCase();
        if (side.equals("CLIENT")) {
            return FMLCommonHandler.instance().getSide().isClient();
        }
        if (side.equals("SERVER")) {
            return FMLCommonHandler.instance().getSide().isServer();
        }
        GroovyLog.get().error("Side processor argument must be CLIENT or SERVER (lower case is allowed too)");
        return true;
    }

    private static boolean checkPackmode(String[] modes) {
        for (String mode : modes) {
            if (Packmode.getPackmode().equals(Alias.autoConvertTo(mode, CaseFormat.LOWER_UNDERSCORE))) {
                return true;
            }
        }
        return false;
    }
}
