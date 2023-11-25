package com.cleanroommc.groovyscript.packmode;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.helper.Alias;
import com.google.common.base.CaseFormat;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Packmode {

    private static final Map<WorldInfo, String> WORLD_PACKMODE = new Object2ObjectOpenHashMap<>();
    private static String packmode;

    @NotNull
    public static String getPackmode() {
        if (hasPackmode()) return Packmode.packmode;
        if (needsPackmode()) {
            throw new IllegalStateException("Tried to get packmode which is currently empty!");
        }
        return StringUtils.EMPTY;
    }

    public static boolean hasPackmode() {
        return packmode != null && !packmode.isEmpty();
    }

    public static boolean needsPackmode() {
        return GroovyScript.getRunConfig().arePackmodesConfigured();
    }

    public static void updatePackmode(String packmode) {
        if (!isValidPackmode(packmode)) throw new IllegalArgumentException("Undefined packmode '" + packmode + "'");
        Packmode.packmode = Alias.autoConvertTo(packmode, CaseFormat.LOWER_UNDERSCORE);
        GroovyScript.getRunConfig().writeAndSavePackmode(Packmode.packmode);
    }

    public static boolean isValidPackmode(String mode) {
        return GroovyScript.getRunConfig().isValidPackmode(mode);
    }

    public static void setWorldPackmode(WorldInfo worldInfo, String packmode) {
        WORLD_PACKMODE.put(worldInfo, packmode);
    }

    public static String getWorldPackmode(WorldInfo worldInfo) {
        return WORLD_PACKMODE.get(worldInfo);
    }

    public static class ChangeEvent extends Event {

    }
}
