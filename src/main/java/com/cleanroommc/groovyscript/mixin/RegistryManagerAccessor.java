package com.cleanroommc.groovyscript.mixin;

import com.google.common.collect.BiMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RegistryManager.class, remap = false)
public interface RegistryManagerAccessor {

    @Accessor
    BiMap<ResourceLocation, ForgeRegistry<? extends IForgeRegistryEntry<?>>> getRegistries();
}
