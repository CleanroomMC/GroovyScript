package com.cleanroommc.groovyscript.core.mixin.erebus;

import erebus.recipes.ComposterRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = ComposterRegistry.class, remap = false)
public interface ComposterRegistryAccessor {

    @Accessor("compostableMaterials")
    static List<Material> getMaterial() {
        throw new UnsupportedOperationException();
    }

    @Accessor("registry")
    static List<ItemStack> getRegistry() {
        throw new UnsupportedOperationException();
    }

    @Accessor("blacklist")
    static List<ItemStack> getBlacklist() {
        throw new UnsupportedOperationException();
    }
}
