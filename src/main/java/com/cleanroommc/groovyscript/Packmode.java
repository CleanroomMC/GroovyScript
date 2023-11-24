package com.cleanroommc.groovyscript;

import com.cleanroommc.groovyscript.helper.Alias;
import com.google.common.base.CaseFormat;
import net.minecraftforge.fml.common.eventhandler.Event;

public class Packmode {

    private static String packmode;

    public static String getPackmode() {
        return packmode;
    }

    public static void updatePackmode(String packmode) {
        if (!isValidPackmode(packmode)) throw new IllegalArgumentException("Undefined packmode '" + packmode + "'");
        Packmode.packmode = Alias.autoConvertTo(packmode, CaseFormat.LOWER_UNDERSCORE);
        GroovyScript.getRunConfig().writeAndSavePackmode(Packmode.packmode);
    }

    public static boolean isValidPackmode(String mode) {
        return GroovyScript.getRunConfig().isValidPackmode(mode);
    }

    public static class ChangeEvent extends Event {

    }
}
