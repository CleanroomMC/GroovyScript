package com.cleanroommc.groovyscript.compat.mods.prodigytech;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.compat.content.GroovyItem;
import lykrast.prodigytech.common.item.IZorrasteelEquipment;
import lykrast.prodigytech.common.recipe.ZorraAltarManager;

public class EnchantableZorraItem extends GroovyItem implements IZorrasteelEquipment {
    private final String registry;

    @GroovyBlacklist
    public EnchantableZorraItem(String loc, String registry) {
        super(loc);
        this.registry = registry;
        ZorraAltar.managers.put(registry, new ZorraAltarManager());
    }

    @Override
    public ZorraAltarManager getManager() {
        return ZorraAltar.managers.get(this.registry);
    }
}
