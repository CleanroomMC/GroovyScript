package com.cleanroommc.groovyscript.sandbox;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Preprocessor {

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
        if (line.startsWith("NO_RUN")) {
            return false;
        }
        if (line.startsWith("DEBUG_ONLY")) {
            return GroovyScript.getRunConfig().isDebug();
        }
        if (line.startsWith("NO_RELOAD")) {
            return !ReloadableRegistryManager.isFirstLoad();
        }
        if (line.startsWith("MODS_LOADED")) {
            return checkModsLoaded(getArguments(line));
        }
        if (line.startsWith("SIDE")) {
            return checkSide(getArguments(line));
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

    private static String[] getArguments(String line) {
        String[] parts = line.split(":", 2);
        if (parts.length < 2) {
            return new String[0];
        }
        String[] args = parts[1].split(",");
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].trim();
        }
        return args;
    }
}
