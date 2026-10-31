package com.cleanroommc.groovyscript.compat.mods.iceandfire;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.github.alexthe666.iceandfire.recipe.IafRecipeRegistry;
import net.minecraftforge.fml.common.Loader;

public class IceAndFire extends GroovyPropertyContainer {

    public final FireForge fireForge;
    public final IceForge iceForge;
    public final LightningForge lightningForge;

    public IceAndFire() {
        fireForge = new FireForge(IafRecipeRegistry.FIRE_FORGE_RECIPES);
        iceForge = new IceForge(IafRecipeRegistry.ICE_FORGE_RECIPES);
        lightningForge = hasLightningForge() ? new LightningForge(IafRecipeRegistry.LIGHTNING_FORGE_RECIPES) : null;
    }

    private static boolean hasLightningForge() {
        var entry = Loader.instance().getIndexedModList().get("iceandfire");
        if (entry == null) return false;
        // RotN Edition changes the name to "Ice And Fire: RotN Edition"
        if (entry.getName().contains("RotN")) return true;
        // RLCraft Edition doesn't change the name but increments the version to 2.x
        return entry.getVersion().startsWith("2");
    }

    @Deprecated
    public static boolean isRotN() {
        var entry = Loader.instance().getIndexedModList().get("iceandfire");
        if (entry == null) return false;
        // Name should be "Ice And Fire: RotN Edition"
        return entry.getName().contains("RotN");
    }
}