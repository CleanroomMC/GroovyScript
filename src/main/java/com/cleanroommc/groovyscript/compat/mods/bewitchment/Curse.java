package com.cleanroommc.groovyscript.compat.mods.bewitchment;

import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.registry.ForgeRegistryWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class Curse extends ForgeRegistryWrapper<com.bewitchment.api.registry.Curse> {
    public Curse() {
        super(GameRegistry.findRegistry(com.bewitchment.api.registry.Curse.class), Alias.generateOfClass(Curse.class));
    }
}
