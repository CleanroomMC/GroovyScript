package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Ritual extends ForgeRegistryWrapper<com.bewitchment.api.registry.Ritual> {
    public Ritual() {
        super(GameRegistry.findRegistry(com.bewitchment.api.registry.Ritual.class));
    }
}
