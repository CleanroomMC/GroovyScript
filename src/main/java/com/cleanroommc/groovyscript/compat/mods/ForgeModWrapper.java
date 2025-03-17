package com.cleanroommc.groovyscript.compat.mods;

import com.cleanroommc.groovyscript.helper.ingredient.ItemsIngredient;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class ForgeModWrapper {

    private ModContainer container;
    private ItemsIngredient items;

    public ForgeModWrapper(ModContainer container) {
        this.container = container;
    }

    public ForgeModWrapper() {}

    void initialize(String owner) {
        this.container = Loader.instance().getIndexedModList().get(owner);
        if (this.container == null) {
            throw new IllegalStateException("Can't create property container for unloaded mod!");
        }
    }

    public ItemsIngredient getAllItems() {
        if (this.items == null) {
            List<ItemStack> items = new ArrayList<>();
            NonNullList<ItemStack> stacks = NonNullList.create();
            for (Item item : ForgeRegistries.ITEMS) {
                if (item.getRegistryName().getNamespace().equals(container.getModId())) {
                    item.getSubItems(CreativeTabs.SEARCH, stacks);
                    items.addAll(stacks);
                    stacks.clear();
                }
            }
            this.items = new ItemsIngredient(items);
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
