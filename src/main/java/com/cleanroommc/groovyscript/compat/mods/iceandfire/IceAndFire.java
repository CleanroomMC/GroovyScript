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
        for (var container : Loader.instance().getActiveModList()) {
            if ("iceandfire".equals(container.getModId())) {
                // Name should be "Ice And Fire: RotN Edition"
                return container.getName().contains("RotN");
            }
        }
        return false;
    }
}
