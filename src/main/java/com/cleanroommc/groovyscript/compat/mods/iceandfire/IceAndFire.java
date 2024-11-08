package com.cleanroommc.groovyscript.compat.mods.iceandfire;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import net.minecraftforge.fml.common.Loader;

public class IceAndFire extends GroovyPropertyContainer {

    public final FireForge fireForge = new FireForge();
    public final IceForge iceForge = new IceForge();
    public final LightningForge lightningForge;

    public IceAndFire() {
        lightningForge = isRotN() ? new LightningForge() : null;
    }

    public static boolean isRotN() {
        var entry = Loader.instance().getIndexedModList().get("iceandfire");
        if (entry == null) return false;
        // Name should be "Ice And Fire: RotN Edition"
        return entry.getName().contains("RotN");
    }
}
