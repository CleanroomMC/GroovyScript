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

        @Config.Comment("Validate input stack sizes when adding recipes, i.e. disallow adding recipes with input stack size > 1 when the machine's code doesn't check for input count. Disable if you want to write such recipes anyway.")
        public boolean checkInputStackCounts = true;

        @Config.Name("ExtendedCrafting recipe maker makes grs recipes")
        @Config.Comment("If this is true, the recipe maker from ExtendedCrafting will produce a script for GroovyScript instead of CraftTweaker.")
        public boolean extendedCraftingRecipeMakerMakesGrsRecipes = true;

        @Config.Name("Blacklisted Containers")
        @Config.Comment({
                "This is a list of modids to not register containers for. Only do this if the containers cause errors or crash when registered.",
                "This happens when a mod uses the same modid as a mod with direct compat, but has different internals,",
                "typically a fork of the original mod that breaks compatibility."
        })
        @Config.RequiresMcRestart
        public String[] blacklistedContainers = {};
    }
}
