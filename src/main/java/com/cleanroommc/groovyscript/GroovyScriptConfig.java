package com.cleanroommc.groovyscript;

import net.minecraftforge.common.config.Config;
import org.jetbrains.annotations.ApiStatus;

@Config(modid = GroovyScript.ID)
public class GroovyScriptConfig {

    @ApiStatus.Internal
    @Config.Comment("The current set packmode")
    public static String packmode = "";
}
