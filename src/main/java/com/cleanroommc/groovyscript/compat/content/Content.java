package com.cleanroommc.groovyscript.compat.content;

import com.cleanroommc.groovyscript.GroovyScript;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.registry.NamedRegistry;
import groovy.lang.Closure;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class Content extends NamedRegistry {

    public CreativeTabs defaultTab;

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

    public void registerFluid(Fluid fluid) {
        FluidRegistry.registerFluid(fluid);
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

    public GroovyFluid.Builder createFluid(String name) {
        return new GroovyFluid.Builder(name);
    }

    public CreativeTabs createCreativeTab(String name, ItemStack icon) {
        return new CreativeTabs(name) {

            @Override
            public @NotNull ItemStack createIcon() {
                return icon.copy();
            }
        };
    }

    public CreativeTabs createCreativeTab(String name, Supplier<ItemStack> icon) {
        return new CreativeTabs(name) {

            @Override
            public @NotNull ItemStack createIcon() {
                return icon.get().copy();
            }
        };
    }

    public CreativeTabs createCreativeTab(String name, Closure<ItemStack> icon) {
        return new CreativeTabs(name) {

            @Override
            public @NotNull ItemStack createIcon() {
                return icon.call().copy();
            }
        };
    }

    public CreativeTabs createCreativeTab(String name, Item icon) {
        return createCreativeTab(name, new ItemStack(icon));
    }

    public CreativeTabs getDefaultTab() {
        return defaultTab;
    }

    public void setDefaultCreativeTab(CreativeTabs tab) {
        this.defaultTab = tab;
    }
}
