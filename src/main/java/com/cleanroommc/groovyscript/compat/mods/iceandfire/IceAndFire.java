package com.cleanroommc.groovyscript.compat.mods.iceandfire;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.github.alexthe666.iceandfire.item.IafDragonForgeRecipeRegistry;
import com.github.alexthe666.iceandfire.recipe.IafRecipeRegistry;
import net.minecraftforge.fml.common.Loader;

public class IceAndFire extends GroovyPropertyContainer {

    public final FireForge fireForge;
    public final IceForge iceForge;
    public final LightningForge lightningForge;

    public IceAndFire() {
        Version version = version();
        fireForge = new FireForge(version == Version.RLCRAFT ? IafDragonForgeRecipeRegistry.FIRE_FORGE_RECIPES : IafRecipeRegistry.FIRE_FORGE_RECIPES);
        iceForge = new IceForge(version == Version.RLCRAFT ? IafDragonForgeRecipeRegistry.ICE_FORGE_RECIPES : IafRecipeRegistry.ICE_FORGE_RECIPES);
        lightningForge = switch (version) {
            case ORIGINAL -> null;
            case ROTN -> new LightningForge(IafRecipeRegistry.LIGHTNING_FORGE_RECIPES);
            case RLCRAFT -> new LightningForge(IafDragonForgeRecipeRegistry.LIGHTNING_FORGE_RECIPES);
        };
    }

    private static Version version() {
        var entry = Loader.instance().getIndexedModList().get("iceandfire");
        if (entry == null) return Version.ORIGINAL;
        // Name should be "Ice And Fire: RotN Edition"
        if (entry.getName().contains("RotN")) return Version.ROTN;
        // Ice And Fire 2.x most likely means RLCraft edition
        else if (entry.getVersion().startsWith("2")) return Version.RLCRAFT;
        return Version.ORIGINAL;
    }

    @Deprecated
    public static boolean isRotN() {
        var entry = Loader.instance().getIndexedModList().get("iceandfire");
        if (entry == null) return false;
        // Name should be "Ice And Fire: RotN Edition"
        return entry.getName().contains("RotN");
    }

    enum Version {
        ORIGINAL,
        ROTN,
        RLCRAFT;
    }
}