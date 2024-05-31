package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Brew extends ForgeRegistryWrapper<com.bewitchment.api.registry.Brew> {
    public Brew() {
        super(GameRegistry.findRegistry(com.bewitchment.api.registry.Brew.class));
    }
}
