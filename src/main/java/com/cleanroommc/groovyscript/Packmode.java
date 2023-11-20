package com.cleanroommc.groovyscript;

import net.minecraftforge.fml.common.eventhandler.Event;

public class Packmode {

    private static String packmode;

    public static String getPackmode() {
        return packmode;
    }

    public static void updatePackmode(String packmode) {
        Packmode.packmode = packmode;
    }

    public static class ChangeEvent extends Event {

    }
}
