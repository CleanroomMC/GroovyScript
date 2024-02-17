package com.cleanroommc.groovyscript;

import net.minecraftforge.common.config.Config;
import org.jetbrains.annotations.ApiStatus;

@Config(modid = GroovyScript.ID)
public class GroovyScriptConfig {

    @ApiStatus.Internal
    @Config.Comment("The current set packmode")
    public static String packmode = "";

    @ApiStatus.Internal
    @Config.Comment("Enable the language server")
    public static boolean server = true; // TODO: change to false

    @ApiStatus.Internal
    @Config.Comment("Listening port for the language server")
    @Config.RangeInt(min = 1, max = 65535)
    public static int port = 8000; // TODO: change port
}
