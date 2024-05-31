package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Tarot extends ForgeRegistryWrapper<com.bewitchment.api.registry.Tarot> {
    public Tarot() {
        super(GameRegistry.findRegistry(com.bewitchment.api.registry.Tarot.class));
    }
}
