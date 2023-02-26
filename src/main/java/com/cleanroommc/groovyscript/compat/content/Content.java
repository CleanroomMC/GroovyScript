package com.cleanroommc.groovyscript.compat.content;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;

public class Content {

    public void registerItem(@Nullable String name, Item item) {
        if (name != null) {
            item.setRegistryName(GroovyScript.getRunConfig().getPackId(), name);
        } else if (item.getRegistryName() == null) {
            GroovyLog.get().errorMC("Can't register item without a name!");
            return;
        }
        GroovyItem.registerItem(item);
    }

    public GroovyItem createItem(String name) {
        return new GroovyItem(name);
    }
}
