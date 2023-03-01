package com.cleanroommc.groovyscript.compat.content;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
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

    public void registerBlock(String name, Block block, ItemBlock item) {
        block.setRegistryName(GroovyScript.getRunConfig().getPackId(), name);
        item.setRegistryName(GroovyScript.getRunConfig().getPackId(), name);
        GroovyBlock.register(block, item);
    }

    public void registerBlock(String name, Block block) {
        registerBlock(name, block, new ItemBlock(block));
    }

    public GroovyItem createItem(String name) {
        return new GroovyItem(name);
    }

    public GroovyBlock createBlock(String name, Material material) {
        return new GroovyBlock(name, material);
    }

    public GroovyBlock createBlock(String name) {
        return new GroovyBlock(name, Material.ROCK);
    }
}
