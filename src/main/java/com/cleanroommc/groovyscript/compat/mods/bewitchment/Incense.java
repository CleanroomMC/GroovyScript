package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Incense extends ForgeRegistryWrapper<com.bewitchment.api.registry.Incense> {
    public Incense() {
        super(GameRegistry.findRegistry(com.bewitchment.api.registry.Incense.class));
    }
}
