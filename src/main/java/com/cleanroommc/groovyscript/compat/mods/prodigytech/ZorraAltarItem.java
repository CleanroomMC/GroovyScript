package com.cleanroommc.groovyscript.compat.mods.prodigytech;

import com.cleanroommc.groovyscript.registry.NamedRegistry;

public class ZorraAltarItem extends NamedRegistry {
    public EnchantableZorraItem item(String loc, String registry) {
        return new EnchantableZorraItem(loc, registry);
    }
}
