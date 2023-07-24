package com.cleanroommc.groovyscript.helper;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.registry.ReloadableRegistryManager;
import com.cleanroommc.groovyscript.sandbox.LoadStage;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

import java.io.File;

public class GroovyHelper {

    @GroovyBlacklist
    private GroovyHelper() {
    }

    public static boolean isLoaded(String mod) {
        return Loader.isModLoaded(mod);
    }

    public static LoadStage getLoadStage() {
        return GroovyScript.getSandbox().getCurrentLoader();
    }

    public static boolean isReloading() {
        return getLoadStage().isReloadable() && !ReloadableRegistryManager.isFirstLoad();
    }

    public static String getMinecraftVersion() {
        return GroovyScript.MC_VERSION;
    }

    public static String getGroovyVersion() {
        return GroovyScript.GROOVY_VERSION;
    }

    public static String getGroovyScriptVersion() {
        return GroovyScript.VERSION;
    }

    public static String getGrSVersion() {
        return GroovyScript.VERSION;
    }

    public static String getPackName() {
        return GroovyScript.getRunConfig().getPackName();
    }

    public static String getPackId() {
        return GroovyScript.getRunConfig().getPackId();
    }

    public static String getPackVersion() {
        return GroovyScript.getRunConfig().getVersion();
    }

    public static boolean isDebug() {
        return GroovyScript.getRunConfig().isDebug();
    }

    public static File getConfigDir() {
        return Loader.instance().getConfigDir();
    }

    public static boolean isClient() {
        return FMLCommonHandler.instance().getEffectiveSide().isClient();
    }

    public static boolean isServer() {
        return FMLCommonHandler.instance().getEffectiveSide().isServer();
    }

    public static boolean isDedicatedServer() {
        return FMLCommonHandler.instance().getSide().isServer();
    }
}
