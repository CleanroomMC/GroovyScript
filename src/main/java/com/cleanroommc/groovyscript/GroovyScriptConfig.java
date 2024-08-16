package com.cleanroommc.groovyscript;

import net.minecraftforge.common.config.Config;
import org.jetbrains.annotations.ApiStatus;

@Config(modid = GroovyScript.ID)
public class GroovyScriptConfig {

    @ApiStatus.Internal
    @Config.Comment("The current set packmode")
    public static String packmode = "";

    @Config.Comment("Port for the VSC connection. Default: 25564")
    public static int languageServerPort = 25564;

    public static Compat compat = new Compat();

    public static class Compat {

        @Config.Comment("Enables DE energy core compat. Config is mainly for other mods compat.")
        public boolean draconicEvolutionEnergyCore = true;

        @Config.Name("ExtendedCrafting recipe maker makes grs recipes")
        @Config.Comment("If this is true, the recipe maker from ExtendedCrafting will produce a script for GroovyScript instead of CraftTweaker.")
        public boolean extendedCraftingRecipeMakerMakesGrsRecipes = true;
    }
}
