package com.cleanroommc.groovyscript.compat.mods;

import com.google.common.collect.AbstractIterator;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Iterator;

public class BasicGroovyPropertyContainer {

    private ModContainer container;
    private Iterable<ItemStack> items;

    public BasicGroovyPropertyContainer(ModContainer container) {
        this.container = container;
    }

    public BasicGroovyPropertyContainer() {}

    void initialize(String owner) {
        this.container = Loader.instance().getIndexedModList().get(owner);
        if (this.container == null) {
            throw new IllegalStateException("Can't create property container for unloaded mod!");
        }
    }

    public Iterable<ItemStack> getAllItems() {
        if (this.items == null) {
            this.items = () -> new AbstractIterator<>() {

                private final Iterator<Item> iterator = ForgeRegistries.ITEMS.iterator();
                private final NonNullList<ItemStack> currentStacks = NonNullList.create();
                private int i = 0;

                @Override
                protected ItemStack computeNext() {
                    while (i >= currentStacks.size()) {
                        if (!iterator.hasNext()) {
                            return endOfData();
                        }
                        currentStacks.clear();
                        iterator.next().getSubItems(CreativeTabs.SEARCH, currentStacks);
                        i = 0;
                    }
                    return currentStacks.get(i++);
                }
            };
        }
        return this.items;
    }

    public ModContainer getContainer() {
        return container;
    }

    public String getId() {
        return this.container.getModId();
    }

    public String getName() {
        return this.container.getName();
    }

    public String getVersion() {
        return this.container.getVersion();
    }
}
