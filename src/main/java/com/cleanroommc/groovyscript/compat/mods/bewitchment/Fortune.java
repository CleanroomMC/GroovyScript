package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Fortune extends ForgeRegistryWrapper<com.bewitchment.api.registry.Fortune> {
    public Fortune() {
        super(GameRegistry.findRegistry(com.bewitchment.api.registry.Fortune.class));
    }
}
