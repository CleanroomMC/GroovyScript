package com.cleanroommc.groovyscript.packmode;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.GroovyScriptConfig;
import com.cleanroommc.groovyscript.helper.Alias;
import com.google.common.base.CaseFormat;
import io.sommers.packmode.api.PackModeAPI;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class Packmode {

    private static String packmode;

    public static @NotNull String getPackmode() {
        if (GroovyScript.getRunConfig().isIntegratePackmodeMod()) return PackModeAPI.getInstance().getCurrentPackMode();
        if (hasPackmode()) return Packmode.packmode;
        if (needsPackmode()) {
            throw new IllegalStateException("Tried to get packmode which is currently empty!");
        }
        return StringUtils.EMPTY;
    }

    public static boolean hasPackmode() {
        return GroovyScript.getRunConfig().isIntegratePackmodeMod() || (packmode != null && !packmode.isEmpty());
    }

    public static boolean needsPackmode() {
        return GroovyScript.getRunConfig().arePackmodesConfigured() && !GroovyScript.getRunConfig().isIntegratePackmodeMod();
    }

    public static void updatePackmode(String packmode) {
        if (GroovyScript.getRunConfig().isIntegratePackmodeMod()) {
            PackModeAPI.getInstance().setNextRestartPackMode(packmode);
            return;
        }
        if (!isValidPackmode(packmode)) throw new IllegalArgumentException("Undefined packmode '" + packmode + "'");
        Packmode.packmode = Alias.autoConvertTo(packmode, CaseFormat.LOWER_UNDERSCORE);
        GroovyScriptConfig.packmode = Packmode.packmode;
        ConfigManager.load(GroovyScript.ID, Config.Type.INSTANCE);
    }

    public static boolean isValidPackmode(String mode) {
        if (GroovyScript.getRunConfig().isIntegratePackmodeMod()) {
            return PackModeAPI.getInstance().isValidPackMode(mode);
        }
        return GroovyScript.getRunConfig().isValidPackmode(mode);
    }

    public static class ChangeEvent extends Event {

        private final String packmode;

        public ChangeEvent(String packmode) {
            this.packmode = packmode;
        }

        public String getPackmode() {
            return packmode;
        }
    }
}
